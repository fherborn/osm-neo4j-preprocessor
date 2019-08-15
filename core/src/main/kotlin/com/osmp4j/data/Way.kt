package com.osmp4j.data

data class Way(val id: String, val osmId: Long, val start: Long, val end: Long, val distance: Double) : CSVObject<Way> {

    override fun getTokens() = listOf(id, osmId, start, end, distance)

    companion object : CSVObjectFactory<Way> {
        override fun getHeaders() = listOf("id", "osmId", "start", "end", "distance")
        override fun fromTokens(tokens: List<String>) = Way(
                id = tokens[0],
                osmId = tokens[1].toLong(),
                start = tokens[2].toLong(),
                end = tokens[3].toLong(),
                distance = tokens[4].toDouble()
        )
    }

}