package com.osmp4j.data

import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.extensions.isFeature
import com.osmp4j.data.osm.features.OSMHighway
import java.io.Serializable

fun List<String>.joinToCSV(lineBreak: Boolean = true) = joinToString(separator = ",", postfix = if (lineBreak) "\n" else "")
fun String.splitCSV() = split(",")

open class Node(val id: Long, val lat: Double, val lon: Double, val version: Int, val timestamp: String, val uid: Long)
class Highway(id: Long, lat: Double, lon: Double, version: Int, timestamp: String, uid: Long) : Node(id, lat, lon, version, timestamp, uid)

interface CSVNodeConverter<T : Node> : CSVConverter<T> {
    override fun fromCSV(csvLine: String) = fromCSV(csvLine.splitCSV())
    fun isType(osmNode: OSMNode): Boolean
    fun getLabels(): List<String>
    fun fromOSM(value: OSMNode): T


    private fun getQueryLabels() = getLabels().joinToString(separator = ":")
    private fun getCreateLine(lineName: String) = getHeader().zip(getTypes())
            .joinToString(prefix = "CREATE (${typeName()}:${getQueryLabels()} {", postfix = "})") { "${it.first}: ${it.second.wrap("$lineName.${it.first}")}" }


    override fun getQueryLines(file: String) = listOf("LOAD CSV WITH HEADERS ", "FROM \"$file\" AS line", getCreateLine("line"))
}


interface CSVConverter<out T> : Serializable {
    fun typeName(): String
    fun getHeader(): List<String>
    fun getFinalHeader(): String = getHeader().joinToCSV()
    fun toFinalCSVLine(obj: Any): String = toCSV(obj).map { it.toString() }.joinToCSV()
    fun fromCSV(csvLine: String) = fromCSV(csvLine.splitCSV())
    fun toCSV(obj: Any): List<Any>
    fun fromCSV(tokens: List<String>): T
    fun getTypes(): List<Neo4jType>
    fun getQueryLines(file: String): List<String>
}

abstract class ExtendCSVConverter<T : E, E : Node>(private val extend: CSVNodeConverter<E>) : CSVNodeConverter<T> {

    override fun getHeader() = extend.getHeader() + getExtensionHeader()
    abstract fun getExtensionHeader(): List<String>

    override fun getTypes() = extend.getTypes() + getExtensionTypes()
    abstract fun getExtensionTypes(): List<Neo4jType>

    override fun toCSV(obj: Any) = extend.toCSV(obj) + getExtensionValues(obj as T)
    abstract fun getExtensionValues(obj: T): List<Any>

    override fun getLabels() = extend.getLabels() + getExtensionLabels()
    abstract fun getExtensionLabels(): List<String>
}


object WayConverter : CSVConverter<Way> {

    override fun typeName() = "way"

    override fun getHeader() = listOf("id", "osmId", "start", "end", "distance")
    override fun getTypes() = listOf(Neo4jString, Neo4jInt, Neo4jString, Neo4jString, Neo4jFloat)
    override fun toCSV(obj: Any) = with(obj as Way) { listOf(id, osmId, start, end, distance) }

    override fun fromCSV(tokens: List<String>) = Way(
            id = tokens[0],
            start = tokens[1].toLong(),
            end = tokens[2].toLong(),
            osmId = tokens[3].toLong(),
            distance = tokens[4].toDouble()
    )

    override fun getQueryLines(file: String) = listOf(
            "USING PERIODIC COMMIT 500",
            "LOAD CSV WITH HEADERS",
            "FROM \"$file\" AS line",
            "MATCH (start:Node { id: line.start }),(end:Node { id: line.end})",
            "CREATE (start)-[:WAY { id: line.id, osmId: toInt(line.osmId), distance: toFloat(line.distance) }]->(end)"
    )
}

sealed class Neo4jType(val wrap: (value: String) -> String)
object Neo4jInt : Neo4jType({ "toInteger($it)" })
object Neo4jFloat : Neo4jType({ "toFloat($it)" })
object Neo4jString : Neo4jType({ "toString($it)" })
object AsIs : Neo4jType({ it })

object NodeConverter : CSVNodeConverter<Node> {
    override fun typeName() = "node"
    override fun getHeader() = listOf("id", "lat", "lon", "version", "timestamp", "uid")
    override fun getTypes() = listOf(AsIs, Neo4jFloat, Neo4jFloat, Neo4jInt, Neo4jString, Neo4jInt)
    override fun toCSV(obj: Any) = with(obj as Node) { listOf(id, lat, lon, version, timestamp, uid) }

    override fun isType(osmNode: OSMNode) = true
    override fun getLabels() = listOf("Node")

    override fun fromCSV(tokens: List<String>) = Node(
            id = tokens[0].toLong(),
            lat = tokens[1].toDouble(),
            lon = tokens[2].toDouble(),
            version = tokens[3].toInt(),
            timestamp = tokens[4],
            uid = tokens[5].toLong()
    )

    override fun fromOSM(value: OSMNode) = Node(
            id = value.id,
            lat = value.lat,
            lon = value.lon,
            version = value.version,
            timestamp = value.timestamp,
            uid = value.uid
    )

}

object HighwayConverter : ExtendCSVConverter<Highway, Node>(NodeConverter) {
    override fun isType(osmNode: OSMNode) = osmNode.isFeature(OSMHighway)
    override fun typeName() = "highway"

    override fun fromOSM(value: OSMNode) = Highway(
            id = value.id,
            lat = value.lat,
            lon = value.lon,
            version = value.version,
            timestamp = value.timestamp,
            uid = value.uid
    )

    override fun fromCSV(tokens: List<String>) = Highway(
            id = tokens[0].toLong(),
            lat = tokens[1].toDouble(),
            lon = tokens[2].toDouble(),
            version = tokens[3].toInt(),
            timestamp = tokens[4],
            uid = tokens[5].toLong()
    )

    override fun getExtensionHeader() = listOf<String>()
    override fun getExtensionTypes() = listOf<Neo4jType>()
    override fun getExtensionValues(obj: Highway) = listOf<Any>()
    override fun getExtensionLabels() = listOf("Highway")

}
//Use one of int, long, float, double, boolean, byte, short, char, string, point, date, localtime, time, localdatetime, datetime, and duration point{crs:WGS-84}

//:ID,name,location:point{crs:WGS-84}
//city02,"London","{y:51.507222, x:-0.1275}"

