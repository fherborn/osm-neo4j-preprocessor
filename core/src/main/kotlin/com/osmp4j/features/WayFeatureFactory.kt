package com.osmp4j.features

import com.osmp4j.data.Way
import com.osmp4j.features.core.FeatureFactory
import com.osmp4j.features.core.Neo4jFloat
import com.osmp4j.features.core.Neo4jInt
import com.osmp4j.features.core.Neo4jString

object WayFeatureFactory : FeatureFactory<Way> {

    override fun typeName() = "way"

    override fun getHeader() = listOf("id", "osmId", "start", "end", "distance")
    override fun getTypes() = listOf(Neo4jString, Neo4jInt, Neo4jString, Neo4jString, Neo4jFloat)
    override fun toCSV(obj: Any) = with(obj as Way) { listOf(id, osmId, start, end, distance) }
    override fun getIndex() = "WAY(id)"

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