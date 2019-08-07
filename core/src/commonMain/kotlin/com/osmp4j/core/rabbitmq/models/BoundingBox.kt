package com.osmp4j.core.rabbitmq.models

import com.osmp4j.core.distanceTo
import com.osmp4j.core.toRadiant

data class BoundingBox(var fromLat: Double, var fromLon: Double, var toLat: Double, var toLon: Double) {
    fun widthDegree() = fromLon distanceTo toLon
    fun heightDegree() = fromLat distanceTo toLat
    fun withRadiant() = widthDegree().toRadiant()
    fun heightRadiant() = heightDegree().toRadiant()
}