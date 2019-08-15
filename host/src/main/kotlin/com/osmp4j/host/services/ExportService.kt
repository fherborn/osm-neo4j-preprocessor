package com.osmp4j.host.services

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

    fun startExport(task: TaskInfo) {
        task.exportStarted()
        task.preparationStarted()
        val boxes = task.box.split(0.25)
        sendPreparationRequests(boxes, task)
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

    @RabbitListener(queues = [QueueNames.PREPARATION_RESPONSE])
    private fun onPreparationResponse(response: PreparationResponse) {
        val (files, task) = response
        preparationRequests.decrement(task.id)

        preparationResults[task.id] = (preparationResults[task.id] ?: listOf()) + files

        if (response.task.isPreparationFinished()) {
            task.preparationFinished()
            //TODO Combine result files
            //TODO Start removeFile duplicates

            val finalNodesFile = File("nodes-merged-${task.id}")
            val finalWaysFile = File("ways-merged-${task.id}")

            val nodesFileNames = preparationResults[task.id]?.map { it.nodesFileName } ?: listOf()
            val waysFileNames = preparationResults[task.id]?.map { it.waysFileName } ?: listOf()

            val nodesFiles = nodesFileNames.map { ftpService.download(it) }
            val waysFiles = waysFileNames.map { ftpService.download(it) }

            finalNodesFile.appendCSV(nodesFiles)
            finalWaysFile.appendCSV(waysFiles)

            downloadService.saveForDownload(task, ResultFileHolder(finalNodesFile, finalWaysFile))
        }
    }

    fun File.appendCSV(files : List<File>) = files.forEach { file ->
        var index = 0
        var headerAppended : Boolean = false
        file.forEachLine { line ->
            if (index != 0 || !headerAppended) {
                appendText(line)
                headerAppended = true
                index++
            }
        }
    }

    private fun TaskInfo.isPreparationFinished() = preparationRequests.isZero(id)


    @RabbitListener(queues = [QueueNames.PREPARATION_ERROR])
    private fun onPreparationError(error: PreparationError) {
        when (error) {
            is BoundingBoxToLargeError -> retryPreparation(error.task, error.box)
        }
        preparationRequests.decrement(error.task.id)
    }


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