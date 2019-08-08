package com.osmp4j.host.rabbit

import com.osmp4j.mq.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.ceil
import kotlin.math.min

fun <T, O> HashMap<T, O>.pop(key: T) = get(key)?.also { remove(key) }?:throw NoSuchElementException()

@Service
class PreparationService @Autowired constructor(private val template: RabbitTemplate) {

    private val logger = LoggerFactory.getLogger(PreparationService::class.java)
    private val pendingRequests = hashMapOf<UUID, PreparationRequest>()


    @RabbitListener(queues = [QueueNames.RESPONSE_PREPARATION])
    fun onPreparationResponse(message: PreparationResponse) {
        logger.debug("Preparation Response -> $message")
        val request = pendingRequests.pop(message.id)

        //TODO remove duplicates task creation

        if(pendingRequests.isEmpty())
            logger.debug("Preparation finished")
    }

    @RabbitListener(queues = [QueueNames.ERROR_PREPARATION])
    fun onErrorResponse(message: PreparationError) {
        when(message) {
            is BoxToLargeError -> {
                logger.debug("Box to large, start splitting in smaller tiles.")
                val oldRequest = pendingRequests.pop(message.id)

                val box = oldRequest.boundingBox
                logger.debug("Boxwidth: ${box.widthDegree()} height: ${box.heightDegree()}")

                val newBoxes = splitBoundingBox(box, box.widthDegree()/2, box.heightDegree()/2)
                createAgentRequests(oldRequest.taskName, newBoxes)
            }
            else -> println("Host: Preparation Response -> $message")
        }
    }

    fun prepare(taskName: String, boundingBox: BoundingBox) {
        logger.startedPreparation(taskName)
        val boundingBoxes = splitBoundingBox(boundingBox)
        createAgentRequests(taskName, boundingBoxes)

    }

    private fun splitBoundingBox(boundingBox: BoundingBox, preferredTileWidth: Double = 0.25, preferredTileHeight: Double = 0.25): List<BoundingBox> {


        val tileCountHorizontal = ceil(boundingBox.widthDegree() / preferredTileWidth).toInt()
        val tileWidth = boundingBox.widthDegree() / tileCountHorizontal

        val tileCountVertical = ceil(boundingBox.heightDegree() / preferredTileHeight).toInt()
        val tileHeight = boundingBox.heightDegree() / tileCountVertical

        logger.debug("Tile width: $tileWidth x $tileHeight")

        val startLat = min(boundingBox.fromLat, boundingBox.toLat)
        val startLon = min(boundingBox.fromLon, boundingBox.toLon)

        val startIndices = (0 until tileCountHorizontal) zip (0 until tileCountVertical)

        return startIndices.map { (latIndex, lonIndex) ->
            BoundingBox(
                    startLat + latIndex * tileWidth,
                    startLon + lonIndex * tileHeight,
                    startLat + (latIndex + 1) * tileWidth,
                    startLon + (lonIndex + 1)* tileHeight
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