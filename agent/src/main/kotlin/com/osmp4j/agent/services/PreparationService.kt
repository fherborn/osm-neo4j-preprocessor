package com.osmp4j.agent.services

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.osmp4j.data.CSVService
import com.osmp4j.data.Node
import com.osmp4j.data.Way
import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.elements.OSMWay
import com.osmp4j.data.osm.extensions.distanceTo
import com.osmp4j.data.osm.extensions.filter
import com.osmp4j.data.osm.features.OSMHighway
import com.osmp4j.data.osm.file.OSMRoot
import com.osmp4j.ftp.FTPService
import com.osmp4j.http.*
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
class PreparationService @Autowired constructor(
        private val template: RabbitTemplate,
        private val httpService: HttpService,
        private val ftpService: FTPService
) {

    private val logger = LoggerFactory.getLogger(PreparationService::class.java)

    @RabbitListener(queues = [QueueNames.PREPARATION_REQUEST])
    fun onPreparationRequest(request: PreparationRequest) {
        val (box, task) = request

        //TODO Precheck errors like size. Because the client knows his source(osm) ans his restrictions
//        logger.debug("Received request with ID: ${request.id}, BoundingBox: ${request.box}")

        //        logger.debug("Started checking for error")

        when (val result = download(box)) {
            is DownloadError -> handleError(result, request)
            is DownloadedFile -> startPreparing(result.file, box, task)
        }
    }

    private fun handleError(error: DownloadError, request: PreparationRequest) {
        when (error) {
            is BandwidthExceededError -> bandWidthExceededErrorFound(request)
            is BadRequestError -> boxToLargeErrorFound(request)
            else -> logger.debug("Unknown error.")
        }
    }

    private fun bandWidthExceededErrorFound(request: PreparationRequest) {
        val waitSeconds = 30
        logger.debug("Bandwidth exceeded error, try again in $waitSeconds seconds before try again.")
        Thread.sleep(waitSeconds * 1000L)
        onPreparationRequest(request)
    }

    private fun boxToLargeErrorFound(request: PreparationRequest) {
        logger.debug("Box to large, sending error to host.")
        template.convertAndSend(QueueNames.PREPARATION_ERROR, BoundingBoxToLargeError(request.box, request.task))
    }

    private fun startPreparing(rawFile: File, box: BoundingBox, task: TaskInfo) {

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
        val nodesFileName = "NODES-${rawFile.name}.csv"
        val nodesToWrite = reducedNodes.map { Node(it.id, it.lat, it.lon) }
        csvService.write(nodesFileName, nodesToWrite, Node)
        val nodesFile = File(nodesFileName)

        //TMP
        // 1, 2, 3
        // 4, 5, 6
        // (1,4), (2,5), (3,6)
        val ways = sequenceOf<Way>()//reducedNodes zip (reducedNodes.drop(1) + reducedNodes.first())).map { Way(it.id, it.first.id, it.second.id, 200.0) }
        val waysFileName = "WAYS-${rawFile.name}.csv"
        csvService.write(waysFileName, ways, Way)
        val waysFile = File(waysFileName)
        //END TMP



        logger.debug("Nodes: ${reducedNodes.count()}")


//        val preprocessedFile = File("${UUID.randomUUID()}.xml")
//        preprocessedFile.createNewFile()
//        mapper.writeValue(preprocessedFile.outputStream(), osmFile)

        logger.debug("Deleting local file")

        //TODO each agent own folder
        upload(nodesFile, waysFile)

        val files = ResultFileHolder(nodesFileName, waysFileName)
        publish(files, task)
        rawFile.delete()
    }

    private fun upload(nodesFile: File, waysFile: File) {
        logger.debug("Started uploading")
        ftpService.upload(nodesFile.name, nodesFile)
        ftpService.upload(waysFile.name, waysFile)
        logger.debug("Finished uploading")
    }

    private fun Sequence<OSMWay>.getStartAndEndNodes(allNodes: Sequence<OSMNode>) = mapNotNull { it.nd }
            .flatMap { sequenceOf(it.first(), it.last()) }
            .map { it.ref }
            .distinct()
            .mapNotNull { ref -> allNodes.find { it.id == ref } }


    private fun Sequence<OSMWay>.reduceWays(reducedNodes: Sequence<OSMNode>, allNodes: Sequence<OSMNode>) = flatMap { it.reduce(reducedNodes, allNodes) }

    private fun OSMWay.reduce(reducedNodes: Sequence<OSMNode>, allNodes: Sequence<OSMNode>): Sequence<OSMWay> {

        val subWays = mutableListOf<Way>()

        val wayNodes = nd.mapNotNull { ref -> allNodes.find { it.id == ref.ref } } // TODO improve performance

        var prevNode: OSMNode? = null
        var currentStartNode: OSMNode? = null
        var distance = 0.0


        wayNodes.forEach { current ->
            val isRelevant = reducedNodes.contains(current)
            val prev = prevNode
            val start = currentStartNode

            if (!isRelevant && prev == null) {
                prevNode = current
            } else if (isRelevant && prev == null) {
                currentStartNode = current
            } else if (isRelevant && prev != null && start == null) {
                currentStartNode = current
                distance = 0.0
            } else if (!isRelevant && prev != null && start != null) {
                distance += prev distanceTo current
            } else if (isRelevant && prev != null && start != null) {
                distance += prev distanceTo current
                subWays.add(Way(id, start.id, current.id, distance))
                currentStartNode = current
                distance = 0.0
            }
            prevNode = current


        }


//        val ways = nd
//                .mapNotNull { ref -> allNodes.find { it.id == ref.ref } }
//                .forEach { current ->
//                    val isRelevant = reducedNodes.contains(current)
//                    val prev = prevNode
//                    val start = currentStartNode
//                    when {
//                        prev != null && start == null && isRelevant -> {
//                            currentStartNode = current
//                            prevNode = current
//                        }
//                        prev != null && start != null && isRelevant -> {
//                            distance += prev distanceTo current
//                            finalWays.add(Way(this.id, start.id, current.id, distance))
//                            distance = 0.0
//                            currentStartNode = current
//                            prevNode = current
//                        }
//                        prev != null && start != null && !isRelevant -> {
//                            distance += prev distanceTo current
//                            prevNode = current
//                        }
//                        prev == null && start == null && isRelevant -> {
//                            currentStartNode = current
//                            distance = 0.0
//                            prevNode = current
//                        }
//                        prev == null && start != null && !isRelevant -> {
//                            prevNode = current
//                        }
//                    }
//
//
//
//                    if(prevNode == null){
//                        prevNode = node
//                    } else {
//                        when {
//                            isRelevant && currentStartNode == null -> {
//                                currentStartNode = node
//                                distance = 0.0
//                            }
//                        }
//                        if(!isRelevant) {
//                            distance +=
//                        }
//                    }
//                }
//
//


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

    private fun download(boundingBox: BoundingBox) =
            httpService.download(getUrl(boundingBox), "${UUID.randomUUID()}.txt")


    private fun getUrl(boundingBox: BoundingBox) =
            "https://www.openstreetmap.org/api/0.6/map?bbox=${boundingBox.fromLat},${boundingBox.fromLon},${boundingBox.toLat},${boundingBox.toLon}"

    private fun publish(files: ResultFileHolder, task: TaskInfo) {
        logger.debug("Started sending to host")
        template.convertAndSend(QueueNames.PREPARATION_RESPONSE, PreparationResponse(files, task))
        logger.debug("Finished sending to host")
    }

    private fun TaskInfo.debug(text: String) { logger.debug("TaskInfo: $id -> $text") }

}