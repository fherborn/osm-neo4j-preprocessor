package com.osmp4j.mq

import com.osmp4j.geo.distanceInKm
import com.osmp4j.geo.distanceTo
import com.osmp4j.geo.toRadiant
import java.io.Serializable
import kotlin.math.min

data class BoundingBox(var fromLat: Double, var fromLon: Double, var toLat: Double, var toLon: Double) : Serializable {

    fun widthDegree() = fromLon distanceTo toLon
    fun heightDegree() = fromLat distanceTo toLat
    fun widthRadiant() = widthDegree().toRadiant()
    fun heightRadiant() = heightDegree().toRadiant()
    fun minWidthInKm() = min(hDistance(fromLon), hDistance(toLon))
    fun minHeightInKm() = min(vDistance(fromLat), vDistance(toLat))

    private fun hDistance(lon: Double) = distanceInKm(fromLat, lon, toLat, lon)
    private fun vDistance(lat: Double) = distanceInKm(lat, fromLon, lat, toLon)

    companion object {
        fun createFixed(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): BoundingBox {
            val (fixedFromLat, fixedToLat) = extractFixedLatitudes(fromLat, toLat)
            val (fixedFromLon, fixedToLon) = getFixedLongitudes(fromLon, toLon)
            return BoundingBox(fixedFromLat, fixedFromLon, fixedToLat, fixedToLon)
        }

        private fun getFixedLongitudes(fromLon: Double, toLon: Double) = if (fromLon < toLon) (fromLon to toLon) else (toLon to fromLon)
        private fun extractFixedLatitudes(fromLat: Double, toLat: Double) = if (fromLat < toLat) (fromLat to toLat) else (toLat to fromLat)
    }
}