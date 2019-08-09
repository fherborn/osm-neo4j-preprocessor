package com.osmp4j.agent.services

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.osmp4j.agent.models.OsmRoot
import com.osmp4j.ftp.FTPService
import com.osmp4j.http.HttpService
import com.osmp4j.messages.BoundingBoxToLargeError
import com.osmp4j.messages.PreparationError
import com.osmp4j.messages.PreparationRequest
import com.osmp4j.messages.PreparationResponse
import com.osmp4j.mq.BoundingBox
import com.osmp4j.mq.QueueNames
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.util.*


fun File.readFirstLine(): String = bufferedReader().use { it.readLine() }

private const val BOX_TO_LARGE_ERROR_START = "You requested too many nodes"

@Service
class PreparationService @Autowired constructor(
        private val template: RabbitTemplate,
        private val httpService: HttpService,
        private val ftpService: FTPService
) {

    private val logger = LoggerFactory.getLogger(PreparationService::class.java)

    @RabbitListener(queues = [QueueNames.PREPARATION_REQUEST])
    fun onPreparationRequest(request: PreparationRequest) {

        //TODO Precheck errors like size. Because the client knows his source(osm) ans his restrictions

        logger.debug("Received request with ID: ${request.id}, BoundingBox: ${request.boundingBox}")

        val boundingBox = request.boundingBox

        val file = download(boundingBox)

        logger.debug("Started checking for error")

        when {
            file.readFirstLine().startsWith(BOX_TO_LARGE_ERROR_START) -> boxToLargeErrorFound(boundingBox, request)
            else -> startPreparing(file, request)
        }
    }

    private fun startPreparing(rawFile: File, request: PreparationRequest) {

        val mapper = XmlMapper()
        mapper.registerModule(ParameterNamesModule())
        mapper.registerModule(KotlinModule())
        val osmFile = mapper.readValue<OsmRoot>(rawFile.inputStream())
        logger.debug("Node count: ${osmFile.node.count()}")

        logger.debug("Deleting local file")
        rawFile.delete()
        publish(rawFile, request)
    }

    private fun boxToLargeErrorFound(boundingBox: BoundingBox, request: PreparationRequest) {
        logger.debug("Box to large, sending error to host.")
        sendError(BoundingBoxToLargeError(boundingBox, request.id))
    }

    private fun sendError(error: PreparationError) = template.convertAndSend(QueueNames.PREPARATION_ERROR, error)

    private fun download(boundingBox: BoundingBox) =
            httpService.download(getUrl(boundingBox), "${UUID.randomUUID()}.txt")


    private fun getUrl(boundingBox: BoundingBox) =
            "https://www.openstreetmap.org/api/0.6/map?bbox=${boundingBox.fromLat},${boundingBox.fromLon},${boundingBox.toLat},${boundingBox.toLon}"

    private fun publish(file: File, request: PreparationRequest) {

        //TODO each agent own folder
        logger.debug("Started uploading")
        ftpService.upload(file.name, file)
        logger.debug("Finished uploading")

        logger.debug("Started sending to host")
        sendResultToHost(file.name, request)
        logger.debug("Finished sending to host")
    }

    private fun sendResultToHost(fileName: String, request: PreparationRequest) {
        template.convertAndSend(QueueNames.PREPARATION_RESPONSE, PreparationResponse(fileName, request.id))
    }

}