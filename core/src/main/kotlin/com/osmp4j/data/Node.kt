package com.osmp4j.data

data class Node(val id: Long, val lat: Double, val lon: Double) : CSVObject<Node> {

    override fun getTokens() = listOf(id, lat, lon)

    companion object : CSVObjectFactory<Node> {
        override fun getHeaders() = listOf("id", "lat", "lon")

        override fun fromTokens(tokens: List<String>) = Node(
                id = tokens[0].toLong(),
                lat = tokens[1].toDouble(),
                lon = tokens[2].toDouble()
        )
    }
}