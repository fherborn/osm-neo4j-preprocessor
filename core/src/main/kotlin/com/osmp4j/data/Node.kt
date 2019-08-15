package com.osmp4j.data

import java.util.*

data class Node(val osmId: Long, val lat: Double, val lon: Double, val id: UUID = UUID.randomUUID()) : CSVObject<Node> {

    override fun getTokens() = listOf(id, osmId, lat, lon)

    companion object : CSVObjectFactory<Node> {
        override fun getHeaders() = listOf("id", "osmId", "lat", "lon")

        override fun fromTokens(tokens: List<String>) = Node(
                id = UUID.fromString(tokens[0]),
                osmId = tokens[1].toLong(),
                lat = tokens[2].toDouble(),
                lon = tokens[3].toDouble()
        )
    }
}