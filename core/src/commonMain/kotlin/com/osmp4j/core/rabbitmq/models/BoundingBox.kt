package com.osmp4j.core.rabbitmq.models

import com.osmp4j.core.distanceInKm
import com.osmp4j.core.distanceTo
import com.osmp4j.core.toRadiant
import kotlin.math.min

data class BoundingBox(var fromLat: Double, var fromLon: Double, var toLat: Double, var toLon: Double) {
    fun widthDegree() = fromLon distanceTo toLon
    fun heightDegree() = fromLat distanceTo toLat
    fun widthRadiant() = widthDegree().toRadiant()
    fun heightRadiant() = heightDegree().toRadiant()
    fun minWidthInKm() = min(hDistance(fromLon), hDistance(toLon))
    fun minHeightInKm() = min(vDistance(fromLat), vDistance(toLat))

    private fun hDistance(lon: Double) = distanceInKm(fromLat, lon, toLat, lon)
    private fun vDistance(lat: Double) = distanceInKm(lat, fromLon, lat, toLon)
}