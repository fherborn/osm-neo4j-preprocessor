package com.osmp4j.features

import com.osmp4j.data.Node
import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.features.core.*

object NodeFeatureFactory : BaseNodeFeatureFactory<Node> {
    override fun typeName() = "node"
    override fun getHeader() = listOf("id", "lat", "lon", "version", "timestamp", "uid")
    override fun getTypes() = listOf(AsIs, Neo4jFloat, Neo4jFloat, Neo4jInt, Neo4jString, Neo4jInt)
    override fun toCSV(obj: Any) = with(obj as Node) { listOf(id, lat, lon, version, timestamp, uid) }
    override fun getIndexAttribute() = "id"

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