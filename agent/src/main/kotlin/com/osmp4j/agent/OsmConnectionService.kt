package com.osmp4j.agent

import com.osmp4j.http.HttpService
import com.osmp4j.mq.BoundingBox
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class OsmConnectionService @Autowired constructor(){

    private val logger = LoggerFactory.getLogger(OsmConnectionService::class.java)

    fun requestMapData(boundingBox: BoundingBox) {
        val queryBody = boundingBox.toQuery("node")

        val service = HttpService()
        service.download(queryBody)
    }
}

/**
<union>
<query type="node">
<bbox-query e="7.157" n="50.748" s="50.746" w="7.154"/>
</query>
</union>
<print mode="meta"/>
 */

fun BoundingBox.toQuery(type: String) = """
 <union>
    <query type="$type">
        <bbox-query e="$toLat" n="$toLon" s="$fromLon" w="$fromLat"/>
    </query>
</union>
<print mode="meta"/>
""".trimIndent()
