package com.osmp4j.agent

import com.osmp4j.ftp.FTPService
import com.osmp4j.http.HttpService
import com.osmp4j.mq.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

fun File.readFirstLine() = bufferedReader().use { it.readLine() }

private const val BOX_TO_LARGE_ERROR_START = "You requested too many nodes"

@Service
class PreparationService @Autowired constructor(
        private val template: RabbitTemplate,
        private val httpService: HttpService,
        private val ftpService: FTPService
) {

    private val logger = LoggerFactory.getLogger(PreparationService::class.java)

    @RabbitListener(queues = [QueueNames.REQUEST_PREPARATION])
    fun onPreparationRequest(request: PreparationRequest) {


        //TODO Precheck errors like size. Because the client knows his source(osm) ans his restrictions
        logger.debug("Received request with ID: ${request.id}, BoundingBox: ${request.boundingBox}")

        val boundingBox = request.boundingBox
        val url = getUrl(boundingBox)

        val fileName = "${UUID.randomUUID()}.txt"
        val file = httpService.download(url, fileName)

        logger.debug("Started checking for error")

        val potentialError = file.readFirstLine()

        if (potentialError.startsWith(BOX_TO_LARGE_ERROR_START)) {
            logger.debug("Box to large, sending error to host.")
            template.convertAndSend(QueueNames.ERROR_PREPARATION, BoxToLargeError(request.id))
        } else {
            logger.debug("No Errors found.")
            //TODO start preprocessing
            upload(fileName, file)
            sendResultToHost(fileName, request)
        }


        logger.debug("Deleting local file")
        file.delete()


    }

    private fun getUrl(boundingBox: BoundingBox) =
            "https://www.openstreetmap.org/api/0.6/map?bbox=${boundingBox.fromLat},${boundingBox.fromLon},${boundingBox.toLat},${boundingBox.toLon}"

    private fun sendResultToHost(fileName: String, request: PreparationRequest) {
        template.convertAndSend(QueueNames.RESPONSE_PREPARATION, PreparationResponse(fileName, request.id))
    }

    private fun upload(fileName: String, file: File) {
        logger.debug("Started uploading")
        ftpService.upload(fileName, file)
        logger.debug("Finished uploading")
    }

}