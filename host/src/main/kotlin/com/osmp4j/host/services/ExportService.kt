package com.osmp4j.host.services

import com.osmp4j.messages.*
import com.osmp4j.models.BoundingBox
import com.osmp4j.mq.QueueNames
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class ExportService @Autowired constructor(private val template: RabbitTemplate, private val downloadService: DownloadService) {

    private val logger = LoggerFactory.getLogger(ExportService::class.java)

    private val preparationRequests = mutableMapOf<UUID, Int>()
    private val duplicatesRequests = mutableMapOf<UUID, Int>()

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

//        val id = UUID.randomUUID()
//        val nodesFile = "NODES-$id.csv"
//        val waysFile = "WAYS-$id.csv"
//        results[nodesFile] = response.nodesFile
//        results[waysFile] = response.waysFile
//        println("""
//            Download:
//            Nodes -> http://192.168.0.192:8080/downloads/exports/$nodesFile
//            Ways -> http://192.168.0.192:8080/downloads/exports/$waysFile
//            """.trimIndent())

        //TODO save result to combine later

        if (response.task.isPreparationFinished()) {
            task.preparationFinished()
            //TODO Combine result files
            //TODO Start removeFile duplicates
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