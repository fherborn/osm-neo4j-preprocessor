package com.osmp4j.agent.services

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.osmp4j.data.BoundingBox
import com.osmp4j.data.Node
import com.osmp4j.data.Way
import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.elements.OSMWay
import com.osmp4j.data.osm.file.OSMRoot
import com.osmp4j.extensions.distanceTo
import com.osmp4j.extensions.toFile
import com.osmp4j.features.NodeFeatureFactory
import com.osmp4j.features.WayFeatureFactory
import com.osmp4j.features.core.FeatureType
import com.osmp4j.features.core.featureRegistry
import com.osmp4j.ftp.FTPService
import com.osmp4j.http.*
import com.osmp4j.messages.*
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
        when (val result = download(box)) {
            is DownloadError -> handleError(result, request)
            is DownloadedFile -> startPreparing(result.file, task)
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

    fun Long.getKey() = toString().take(2)

    private fun startPreparing(rawFile: File, task: TaskInfo) {

        val (nodes, ways) = getOsmData(rawFile)

        val featureMap = nodes.filterFeatures(task.types)
        val openWays = ways.filterOpenWays()

        //Filter nodes
        val startEndNodes = openWays.getStartEndNodesNotInFeatures(nodes) { !featureMap.contains { ex -> ex.second.equalsOSM(it) } }
        val reducedNodes = (featureMap + startEndNodes).distinctBy { it.second.id }

        //Group for performance
        val allNodesMap = nodes.map { NodeFeatureFactory.fromOSM(it) }.groupBy { it.id.getKey() }
        val reducedNodesMap = reducedNodes.map { it.second }.groupBy { it.id.getKey() }

        //Reduce ways
        val reducedWays = openWays.reduceWays(reducedNodesMap, allNodesMap)

        //Write nodes
        val sortedNodes = reducedNodes.groupBy { it.first }.mapValues { np -> np.value.map { it.second } }
        val nodeFiles = sortedNodes.mapValues { it.value.toFile(featureRegistry(it.key)) }

        //Write ways
        val waysFile = reducedWays.toFile(WayFeatureFactory)

        uploadFiles(nodeFiles.values + waysFile)

        val nodeFileNames = nodeFiles.mapValues { it.value.name }
        publish(ResultFileNameHolder(nodeFileNames, waysFile.name), task)

        logger.debug("Deleting local file")
        nodeFiles.values.forEach { it.delete() }

        rawFile.delete()
        waysFile.delete()
    }

    private fun Sequence<OSMWay>.getStartEndNodesNotInFeatures(nodes: Sequence<OSMNode>, filter: (OSMNode) -> Boolean): Sequence<Pair<FeatureType, Node>> =
            getStartAndEndNodes(nodes).filter(filter).map { FeatureType.NODE to NodeFeatureFactory.fromOSM(it) }


    private fun Node.equalsOSM(osmNode: OSMNode) = id == osmNode.id

    private fun <T> Sequence<T>.contains(predicate: (T) -> Boolean) = find(predicate) != null

    private fun Sequence<OSMWay>.filterOpenWays() = filter { it.nd.first() != it.nd.last() }

    private fun Sequence<OSMNode>.filterFeatures(types: List<FeatureType>): Sequence<Pair<FeatureType, Node>> =
            types.asSequence()
                    .mapNotNull { type -> type to featureRegistry(type) }
                    .flatMap { (type, converter) -> filter { converter.isType(it) }.map { type to converter.fromOSM(it) } }


    private fun getOsmData(rawFile: File): Pair<Sequence<OSMNode>, Sequence<OSMWay>> {
        val osmFile = readOSMFile(rawFile)

        val allNodes = osmFile.node?.asSequence() ?: sequenceOf()
        val allWays = osmFile.way?.asSequence() ?: sequenceOf()
        return Pair(allNodes, allWays)
    }

    private fun readOSMFile(rawFile: File): OSMRoot {
        val mapper = getXmlMapper()

        val osmFile = mapper.readValue<OSMRoot>(rawFile.inputStream())
        return osmFile
    }

    private fun getXmlMapper(): XmlMapper {
        val mapper = XmlMapper()
        mapper.registerModule(ParameterNamesModule())
        mapper.registerModule(KotlinModule())
        return mapper
    }

    private fun uploadFiles(files: List<File>) {
        logger.debug("Started uploading")
        ftpService.execute {
            files.forEach { upload(it) }
        }
        logger.debug("Finished uploading")
    }

    private fun Sequence<OSMWay>.getStartAndEndNodes(allNodes: Sequence<OSMNode>) = mapNotNull { it.nd }
            .flatMap { sequenceOf(it.first(), it.last()) }
            .map { it.ref }
            .distinct()
            .mapNotNull { ref -> allNodes.find { it.id == ref } }


    private fun Sequence<OSMWay>.reduceWays(reducedNodes: Map<String, List<Node>>, allNodes: Map<String, List<Node>>) = flatMap { it.reduce(reducedNodes, allNodes) }

    private fun OSMWay.reduce(reducedNodes: Map<String, List<Node>>, allNodes: Map<String, List<Node>>): Sequence<Way> {

        logger.debug("Start generating subways")
        logger.debug("REDUCED NODES = ${reducedNodes.values.size}")

        val subWays = mutableListOf<Way>()

        var prevNode: Node? = null
        var currentStartNode: Node? = null
        var distance = 0.0


        logger.debug("Generating sub ways")

        var index = 0
        nd
                .asSequence()
                .mapNotNull { ref -> allNodes[ref.ref.getKey()]?.find { it.id == ref.ref } }
                .map { node -> node to (reducedNodes[node.id.getKey()]?.find { it.id == node.id } != null) }
                .forEach { (current, isRelevant) ->
                    val start = currentStartNode
                    if (isRelevant) {
                        if (start != null) {
                            distance += prevNode?.distanceTo(current) ?: 0.0
                            subWays.add(Way("${id}_${index++}", id, start.id, current.id, distance))
                        }
                        distance = 0.0
                        currentStartNode = current
                    } else {
                        distance += prevNode?.distanceTo(current) ?: 0.0
                    }
                    prevNode = current
                }



        logger.debug("Prepared way from 1 to ${subWays.count()}")
        return subWays.asSequence()
    }

    private fun download(boundingBox: BoundingBox) =
            httpService.download(getUrl(boundingBox), "${UUID.randomUUID()}.csv")


    private fun getUrl(boundingBox: BoundingBox) =
            "https://www.openstreetmap.org/api/0.6/map?bbox=${boundingBox.fromLat},${boundingBox.fromLon},${boundingBox.toLat},${boundingBox.toLon}"

    private fun publish(files: ResultFileNameHolder, task: TaskInfo) {
        logger.debug("Started sending to host")
        template.convertAndSend(QueueNames.PREPARATION_RESPONSE, PreparationResponse(files, task))
        logger.debug("Finished sending to host")
    }

    private fun TaskInfo.debug(text: String) {
        logger.debug("TaskInfo: $id -> $text")
    }

}