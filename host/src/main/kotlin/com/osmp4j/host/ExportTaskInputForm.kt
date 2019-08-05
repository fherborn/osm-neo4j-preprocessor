package com.osmp4j.host

import com.osmp4j.core.uuid.UUIDGenerator

data class ExportTaskInputForm(

        var taskName: String = "Task-${UUIDGenerator.randomUUID()}",

        val preferredSplitCount: Int = 20,

        var fromLat: Double = 7.0862,

        var fromLon: Double = 51.0138,

        var toLat: Double = 7.7344,

        var toLon: Double = 51.3134
)