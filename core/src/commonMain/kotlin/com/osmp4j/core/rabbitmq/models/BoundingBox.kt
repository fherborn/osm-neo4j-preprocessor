package com.osmp4j.core.rabbitmq.models

import com.osmp4j.core.rabbitmq.UUIDGenerator

data class BoundingBox(

        var taskName: String = "Task-${UUIDGenerator.randomUUID()}",

        var fromLat: Double = 7.0862,

        var fromLon: Double = 51.0138,

        var toLat: Double = 7.7344,

        var toLon: Double = 51.3134

)