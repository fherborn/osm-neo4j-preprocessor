package com.osmp4j.host.services

import com.osmp4j.data.*
import com.osmp4j.extensions.add
import com.osmp4j.ftp.FTPService
import com.osmp4j.messages.*
import com.osmp4j.models.BoundingBox
import com.osmp4j.mq.QueueNames
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.util.*


@Service
class ExportService @Autowired constructor(private val template: RabbitTemplate, private val downloadService: DownloadService, private val ftpService: FTPService) {

    private val logger = LoggerFactory.getLogger(ExportService::class.java)

    private val preparationRequests = mutableMapOf<UUID, Int>()
    private val duplicatesRequests = mutableMapOf<UUID, Int>()

    private val preparationResults = mutableMapOf<UUID, List<ResultFileNameHolder>>()

    private val duplicateFreeNodeFiles = mutableMapOf<UUID, List<String>>()
    private val duplicateFreeWayFiles = mutableMapOf<UUID, List<String>>()


    @RabbitListener(queues = [QueueNames.DUPLICATES_RESPONSE])
    private fun onDuplicatesResponse(response: DuplicateResponse) {
        val (fileName, fileType, task) = response
        duplicatesRequests.decrement(task.id)

        when (fileType) {
            FileType.NODES -> duplicateFreeNodeFiles.add(task.id, fileName)
            FileType.WAYS -> duplicateFreeWayFiles.add(task.id, fileName)
        }

        if (task.isDuplicatesFinished()) {
            onFinishDuplicates(task)
        }
    }

    @RabbitListener(queues = [QueueNames.DUPLICATES_ERROR])
    private fun onDuplicatesResponse(response: DuplicatesError) {

    }

    @RabbitListener(queues = [QueueNames.PREPARATION_ERROR])
    private fun onPreparationError(error: PreparationError) {
        when (error) {
            is BoundingBoxToLargeError -> retryPreparation(error.task, error.box)
        }
        preparationRequests.decrement(error.task.id)
    }


    @RabbitListener(queues = [QueueNames.PREPARATION_RESPONSE])
    private fun onPreparationResponse(response: PreparationResponse) {
        val (files, task) = response
        preparationRequests.decrement(task.id)
        preparationResults.add(task.id, files)
        if (response.task.isPreparationFinished()) {
            onPreparationFinished(task)
        }
    }

    fun startExport(task: TaskInfo) {
        task.exportStarted()
        task.preparationStarted()
        val boxes = task.box.split(0.25)
        sendPreparationRequests(boxes, task)
    }

    private fun onPreparationFinished(task: TaskInfo) {
        task.preparationFinished()

        val nodesFileNames = preparationResults[task.id]?.map { it.nodesFileName } ?: listOf()
        val waysFileNames = preparationResults[task.id]?.map { it.waysFileName } ?: listOf()

        val (nodesFiles, waysFiles) = ftpService.execute {
            val nodesFiles = nodesFileNames.map { downloadAndDelete(it) }
            val waysFiles = waysFileNames.map { downloadAndDelete(it) }
            nodesFiles to waysFiles
        }


        removeDuplicates(nodesFiles, waysFiles, task)
    }

    private fun onFinishDuplicates(task: TaskInfo) {
        //TODO work with streams

        val (nodeFiles, wayFiles) = ftpService.execute {
            val nodeFiles = duplicateFreeNodeFiles[task.id]?.map { downloadAndDelete(it) } ?: listOf()
            val wayFiles = duplicateFreeWayFiles[task.id]?.map { downloadAndDelete(it) } ?: listOf()
            nodeFiles to wayFiles
        }

        val mergedNodesFile = nodeFiles.mergeCSV("nodes-merged-${task.id}", Node)
        val mergedWaysFile = wayFiles.mergeCSV("ways-merged-${task.id}", Way)

        downloadService.saveForDownload(task, ResultFileHolder(mergedNodesFile, mergedWaysFile))
    }

    private fun sendPreparationRequests(boxes: List<BoundingBox>, task: TaskInfo) {
        boxes.forEach { sendPreparationRequest(it, task) }
    }

    private fun sendPreparationRequest(box: BoundingBox, task: TaskInfo) {
        val request = PreparationRequest(box, task)
        preparationRequests.increment(task.id)
        sendPreparationRequest(request)
    }

    private fun sendPreparationRequest(request: PreparationRequest) {
        template.convertAndSend(QueueNames.PREPARATION_REQUEST, request)
    }

    private fun sendDuplicateRequest(request: DuplicateRequest) {
        template.convertAndSend(QueueNames.DUPLICATES_REQUEST, request)
    }

    private fun removeDuplicates(nodesFiles: List<File>, waysFiles: List<File>, task: TaskInfo) {

        fun getFileGen(prefix: String): (String) -> String = { "$prefix-$it-${UUID.randomUUID()}.csv" }

        val nodesFileGen = getFileGen("nodes")
        val waysFileGen = getFileGen("ways")

        val nodeFileMap = nodesFiles.groupByCSV(Node, nodesFileGen) { it.id.toString().take(2) }
        val wayFileMap = waysFiles.groupByCSV(Way, waysFileGen) { it.id.take(2) }

        ftpService.execute {
            nodeFileMap.values.forEach{ upload(it.name, it) }
            wayFileMap.values.forEach{ upload(it.name, it) }
        }

        nodeFileMap.values.forEach {
            sendDuplicateRequest(it.name, FileType.NODES, task)
            it.delete()
        }

        wayFileMap.values.forEach {
            sendDuplicateRequest(it.name, FileType.WAYS, task)
            it.delete()
        }

        nodesFiles.forEach { it.delete() }
        waysFiles.forEach { it.delete() }

    }

    private fun sendDuplicateRequest(fileName: String, fileType: FileType, task: TaskInfo) {
        val request = DuplicateRequest(fileName, fileType, task)
        duplicatesRequests.increment(task.id)
        sendDuplicateRequest(request)
    }

    private fun TaskInfo.isPreparationFinished() = preparationRequests.isZero(id)
    private fun TaskInfo.isDuplicatesFinished() = duplicatesRequests.isZero(id)


    private fun retryPreparation(task: TaskInfo, box: BoundingBox) {
        task.boxToLarge(box)
        val boxes = box.split(box.width() / 2, box.height() / 2)
        sendPreparationRequests(boxes, task)
    }


    private fun <T> MutableMap<T, Int>.increment(key: T) {
        this[key] = (this[key] ?: 0).plus(1)
    }

    private fun <T> MutableMap<T, Int>.decrement(key: T) {
        this[key] = this[key]?.minus(1) ?: return
    }

    private fun <T> MutableMap<T, Int>.isZero(key: T): Boolean {
        return this[key] ?: 0 == 0
    }

    private fun TaskInfo.exportStarted() = debug("Export of $box started")
    private fun TaskInfo.preparationFinished() = debug("Preparation finished")
    private fun TaskInfo.preparationStarted() = debug("Preparation started")
    private fun TaskInfo.boxToLarge(box: BoundingBox) = debug("Box $box to large, splitting started.")
    private fun TaskInfo.debug(text: String) {
        logger.debug("TaskInfo: $id -> $text")
    }
}