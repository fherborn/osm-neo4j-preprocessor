package com.osmp4j.agent.services

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.osmp4j.data.CSVService
import com.osmp4j.data.Node
import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.elements.OSMWay
import com.osmp4j.data.osm.extensions.filter
import com.osmp4j.data.osm.features.OSMHighway
import com.osmp4j.data.osm.file.OSMRoot
import com.osmp4j.ftp.FTPService
import com.osmp4j.http.*
import com.osmp4j.messages.BoundingBoxToLargeError
import com.osmp4j.messages.PreparationError
import com.osmp4j.messages.PreparationRequest
import com.osmp4j.messages.PreparationResponse
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

        val downloadResult = download(boundingBox)
        logger.debug("Started checking for error")

        when (downloadResult) {
            is DownloadError -> handleError(downloadResult, request)
            is DownloadedFile -> startPreparing(downloadResult.file, request)
        }
    }

    private fun handleError(error: DownloadError, request: PreparationRequest) {
        when (error) {
            is BandwidthExceededError -> bandWidthExceededErrorFound(error, request)
            is BadRequestError -> boxToLargeErrorFound(request.boundingBox, request)
            else -> logger.debug("Unknown error.")
        }
    }

    private fun bandWidthExceededErrorFound(error: BandwidthExceededError, request: PreparationRequest) {
        val waitSeconds = error.timeout + 1
        logger.debug("Bandwidth exceeded error, try again in $waitSeconds seconds before try again.")
        Thread.sleep(waitSeconds * 1000L)
        onPreparationRequest(request)
    }

    private fun startPreparing(rawFile: File, request: PreparationRequest) {
        val mapper = XmlMapper()
        mapper.registerModule(ParameterNamesModule())
        mapper.registerModule(KotlinModule())

        val osmFile = mapper.readValue<OSMRoot>(rawFile.inputStream())

        val allNodes = osmFile.node?.asSequence() ?: sequenceOf()
        val allWays = osmFile.way?.asSequence() ?: sequenceOf()

        val features = allNodes.filter(OSMHighway)
        val startEndNodes = allWays.getStartAndEndNodes(allNodes)
        val reducedNodes = (features + startEndNodes).distinct()

        //val reducedWays = allWays.reduceWays(reducedNodes, allNodes)
        val csvService = CSVService()
        val csvFileName = "CSV-${rawFile.name}.csv"
        val nodesToWrite = reducedNodes.map { Node(it.id, it.lat, it.lon) }
        csvService.write(csvFileName, nodesToWrite, Node)
        val file = File(csvFileName)

        logger.debug("Nodes: ${reducedNodes.count()}")


//        val preprocessedFile = File("${UUID.randomUUID()}.xml")
//        preprocessedFile.createNewFile()
//        mapper.writeValue(preprocessedFile.outputStream(), osmFile)

        logger.debug("Deleting local file")
        publish(rawFile, request)
        publish(file, request)
        rawFile.delete()
    }

    private fun Sequence<OSMWay>.getStartAndEndNodes(allNodes: Sequence<OSMNode>) = mapNotNull { it.nd }
            .flatMap { sequenceOf(it.first(), it.last()) }
            .map { it.ref }
            .distinct()
            .mapNotNull { ref -> allNodes.find { it.id == ref } }


    private fun Sequence<OSMWay>.reduceWays(reducedNodes: Sequence<OSMNode>, allNodes: Sequence<OSMNode>) = asSequence().flatMap { it.reduce(reducedNodes, allNodes) }

    private fun OSMWay.reduce(reducedNodes: Sequence<OSMNode>, allNodes: Sequence<OSMNode>): Sequence<OSMWay> {
//
//        val wayNodes = nd
//                ?.asSequence()
//                ?.map { ref -> allNodes.first { it.id == ref.ref } }
//                ?:sequenceOf()
//
//        val wayParts = (wayNodes zip wayNodes.drop(1))
//                .map { it to it.distance() }
//
//        //TODO shrink ways
//
//        var lastDistance = 0.0
//        var lastNode = wayNodes.first()
//        val ways = mutableListOf<OSMWay>()
        TODO()
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