package com.osmp4j.host.rabbit

import com.osmp4j.mq.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.ceil
import kotlin.math.min

@Service
class PreparationService @Autowired constructor(private val template: RabbitTemplate) {

    private val logger = LoggerFactory.getLogger(PreparationService::class.java)
    private val pendingRequests = hashMapOf<UUID, Request>()


    @RabbitListener(queues = [QueueNames.RESPONSE_PREPARATION])
    fun onPreparationResponse(message: Response) {
        println("Host: Preparation Response -> $message")
    }

    fun prepare(taskName: String, tileSize: Int, boundingBox: BoundingBox) {
        logger.startedPreparation(taskName)
        val boundingBoxes = splitBoundingBox(boundingBox, tileSize)
        createAgentRequests(taskName, boundingBoxes)

    }

    private fun splitBoundingBox(boundingBox: BoundingBox, tileSize: Int): List<BoundingBox> {

        val tileCountHorizontal = ceil(boundingBox.minWidthInKm() / tileSize).toInt()
        val tileWidth = boundingBox.widthDegree() / tileCountHorizontal

        val tileCountVertical = ceil(boundingBox.minHeightInKm() / tileSize).toInt()
        val tileHeight = boundingBox.heightDegree() / tileCountVertical

        val startLat = min(boundingBox.fromLat, boundingBox.toLat)
        val startLon = min(boundingBox.fromLon, boundingBox.toLon)

        val startIndices = (0 until tileCountHorizontal) zip (0 until tileCountVertical)

        return startIndices.map { (latIndex, lonIndex) ->
            BoundingBox(
                    startLat + latIndex * tileWidth,
                    startLon + lonIndex * tileHeight,
                    startLat + latIndex * (tileWidth + 1),
                    startLon + lonIndex * (tileHeight + 1)
            )
        }
    }

    private fun createAgentRequests(taskName: String, boundingBoxes: List<BoundingBox>) {
        boundingBoxes.forEach { sendRequest(PreparationRequest(taskName, it)) }
    }

    private fun sendRequest(request: PreparationRequest) {
        template.convertAndSend(QueueNames.REQUEST_PREPARATION, request)
        addPendingRequest(request)
        logger.agentRequestCreated(request)
    }

    private fun addPendingRequest(request: PreparationRequest) {
        pendingRequests[request.id] = request
    }

    fun Logger.agentRequestCreated(request: PreparationRequest) =
            this.debug("Created request for task: ${request.taskName} with bounding box: " +
                    "${request.boundingBox.fromLat}, ${request.boundingBox.fromLon}, " +
                    "${request.boundingBox.toLat}, ${request.boundingBox.toLon}")

    fun Logger.startedPreparation(taskName: String) = this.debug("Started preparation of task: $taskName")

}