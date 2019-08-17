package com.osmp4j.features

import com.osmp4j.data.Highway
import com.osmp4j.data.Node
import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.features.OSMHighway
import com.osmp4j.extensions.isFeature
import com.osmp4j.features.core.ExtendNodeFeatureFactory
import com.osmp4j.features.core.Neo4jType

object HighwayFeatureFactory : ExtendNodeFeatureFactory<Highway, Node>(NodeFeatureFactory) {

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
    override fun getExtensionLabel() = "Highway"

}