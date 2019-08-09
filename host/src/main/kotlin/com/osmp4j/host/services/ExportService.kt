package com.osmp4j.host.services

import com.osmp4j.extensions.identityMapOf
import com.osmp4j.extensions.pop
import com.osmp4j.extensions.put
import com.osmp4j.host.Task
import com.osmp4j.messages.BoundingBoxToLargeError
import com.osmp4j.messages.PreparationError
import com.osmp4j.messages.PreparationRequest
import com.osmp4j.messages.PreparationResponse
import com.osmp4j.mq.BoundingBox
import com.osmp4j.mq.QueueNames
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class ExportService @Autowired constructor(private val template: RabbitTemplate) {

    private val logger = LoggerFactory.getLogger(ExportService::class.java)
    private val tasks = identityMapOf<Task>()

    private val preparationRequestsTasks = hashMapOf<UUID, UUID>()
    private val duplicateRequestTasks = hashMapOf<UUID, UUID>()

    fun startExport(task: Task) {
        tasks.put(task)
        logger.exportStarted(task.id)
        startPreparation(task)
    }

    @RabbitListener(queues = [QueueNames.PREPARATION_RESPONSE])
    private fun onPreparationResponse(response: PreparationResponse) {
        val taskId = preparationRequestsTasks.pop(response.id)

        //TODO Start remove duplicates

        if (isTaskPreparationFinished(taskId)) {
            finishTaskPreparation(taskId)
        }
    }

    private fun finishTaskPreparation(taskId: UUID) {
        logger.preparationFinished(taskId)
    }

    private fun isTaskPreparationFinished(taskId: UUID) = !preparationRequestsTasks.containsValue(taskId)


    @RabbitListener(queues = [QueueNames.PREPARATION_ERROR])
    private fun onPreparationError(error: PreparationError) {
        val taskId = preparationRequestsTasks.pop(error.id)

        when (error) {
            is BoundingBoxToLargeError -> retryPreparation(taskId, error.boundingBox)
        }
    }


    private fun startPreparation(task: Task) {
        logger.preparationStarted(task.id)
        val boxes = task.box.split(0.25)
        prepare(task.id, boxes)
    }

    private fun retryPreparation(taskId: UUID, box: BoundingBox) {
        logger.boxToLarge(taskId, box)
        val boxes = box.split(box.widthDegree() / 2, box.heightDegree() / 2)
        prepare(taskId, boxes)
    }

    private fun prepare(taskId: UUID, boxes: List<BoundingBox>) {
        boxes.forEach { prepare(taskId, it) }
    }

    fun prepare(taskId: UUID, box: BoundingBox) {
        val request = PreparationRequest(box)
        preparationRequestsTasks[request.id] = taskId
        template.convertAndSend(QueueNames.PREPARATION_REQUEST, request)
    }

    private fun Logger.exportStarted(task: UUID) = task(task) { "Export of ${it.box} started" }
    private fun Logger.preparationFinished(task: UUID) = task(task) { "Preparation finished" }
    private fun Logger.preparationStarted(task: UUID) = task(task) { "Preparation started" }
    private fun Logger.boxToLarge(task: UUID, box: BoundingBox) = task(task) { "Box $box to large, splitting started." }
    private fun Logger.task(taskId: UUID, createMessage: (task: Task) -> String) = tasks[taskId]?.let {
        debug("Task: $taskId -> ${createMessage(it)}")
    }

}