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

    private val duplicateFreeNodeFiles = mutableMapOf<UUID, List<Pair<NodeType, String>>>()
    private val duplicateFreeWayFiles = mutableMapOf<UUID, List<String>>()


    @RabbitListener(queues = [QueueNames.DUPLICATES_RESPONSE])
    private fun onDuplicatesResponse(response: DuplicateResponse) {

        duplicatesRequests.decrement(response.task.id)

        when (response) {
            is NodesDuplicateResponse -> {
                duplicateFreeNodeFiles.add(response.task.id, response.nodeType to response.fileName)
            }
            is WaysDuplicateResponse -> {
                duplicateFreeWayFiles.add(response.task.id, response.fileName)
            }
        }

        if (response.task.isDuplicatesFinished()) {
            onFinishDuplicates(response.task)
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

        val nodesFileNames = preparationResults[task.id]
                ?.map { it.nodeFileNames }
                ?.flatMap { map -> map.map { it.key to it.value } }
                ?.groupBy { it.first }
                ?.mapValues { l -> l.value.map { it.second } }
                ?: mapOf()

        val waysFileNames = preparationResults[task.id]?.map { it.waysFileName } ?: listOf()

        val (nodesFiles, waysFiles) = ftpService.execute {
            val nodesFiles = nodesFileNames.map { nf -> nf.key to nf.value.map { downloadAndDelete(it) } }
            val waysFiles = waysFileNames.map { downloadAndDelete(it) }
            nodesFiles to waysFiles
        }


        removeDuplicates(nodesFiles, waysFiles, task)
    }

    private fun onFinishDuplicates(task: TaskInfo) {
        //TODO work with streams

        logger.debug("FINISH duplicates for ${task.types}")

        val (nodeFiles, wayFiles) = ftpService.execute {
            val nodeFiles = duplicateFreeNodeFiles[task.id]
                    ?.map { (type, file) -> type to downloadAndDelete(file) }
                    ?.groupBy { it.first }
                    ?.mapValues { g -> g.value.map { it.second } }
                    ?: mapOf()
            val wayFiles = duplicateFreeWayFiles[task.id]?.map { downloadAndDelete(it) } ?: listOf()
            nodeFiles to wayFiles
        }

        val mergedNodesFile = nodeFiles.map { g ->
            val converter = getConverter(g.key)
            g.key to g.value.mergeCSV("${converter.typeName()}-merged-${task.id}", converter)
        }
        val mergedWaysFile = wayFiles.mergeCSV("ways-merged-${task.id}", WayConverter)

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

    private fun sendDuplicateRequest(request: DuplicateRequest, task: TaskInfo) {
        duplicatesRequests.increment(task.id)
        template.convertAndSend(QueueNames.DUPLICATES_REQUEST, request)
    }

    private fun removeDuplicates(nodesFiles: List<Pair<NodeType, List<File>>>, waysFiles: List<File>, task: TaskInfo) {

        fun getFileGen(prefix: String): (String) -> String = { "rmd-$prefix-$it-${UUID.randomUUID()}.csv" }

        fun <T> getNodeFileGen(converter: CSVConverter<T>) = getFileGen(converter.typeName())
        val waysFileGen = getFileGen("ways")

        val nodeFileMap = nodesFiles.map { (type, files) ->
            val converter = getConverter(type)
            type to files.groupByCSV(converter, getNodeFileGen(converter)) { it.id.toString().take(2) }
        }
        val wayFileMap = waysFiles.groupByCSV(WayConverter, waysFileGen) { it.id.take(2) }

        ftpService.execute {
            nodeFileMap.forEach { g -> g.second.forEach { upload(it.value) } }
            wayFileMap.values.forEach{ upload(it.name, it) }
        }

        nodeFileMap.forEach { g ->
            g.second.values.forEach { file ->
                val request = NodesDuplicateRequest(file.name, g.first, task)
                sendDuplicateRequest(request, task)
                file.delete()
            }
        }



        wayFileMap.values.forEach { file ->
            val request = WaysDuplicateRequest(file.name, task)
            sendDuplicateRequest(request, task)
            file.delete()
        }

        nodesFiles.flatMap { it.second }.forEach { it.delete() }
        waysFiles.forEach { it.delete() }

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