package com.osmp4j.agent

import com.osmp4j.mq.BoundingBox

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
