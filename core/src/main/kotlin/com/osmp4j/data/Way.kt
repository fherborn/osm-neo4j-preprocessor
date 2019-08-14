package com.osmp4j.data

data class Way(val start: Long, val end: Long, val distance: Double) : CSVObject<Way> {

    override fun getTokens() = listOf(start, end, distance)

    companion object : CSVObjectFactory<Way> {
        override fun getHeaders() = listOf("start", "end", "distance")
        override fun fromTokens(tokens: List<String>) = Way(
                start = tokens[0].toLong(),
                end = tokens[1].toLong(),
                distance = tokens[2].toDouble()
        )
    }

}