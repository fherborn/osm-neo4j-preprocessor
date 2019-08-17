package com.osmp4j.data

import com.osmp4j.geo.distanceTo
import java.io.Serializable
import kotlin.math.ceil
import kotlin.math.min

data class BoundingBox internal constructor(val fromLat: Double, val fromLon: Double, val toLat: Double, val toLon: Double) : Serializable {

    fun width() = fromLon distanceTo toLon

    fun height() = fromLat distanceTo toLat


    fun split(tileWidth: Double, tileHeight: Double = tileWidth): List<BoundingBox> {

        val tileCountHorizontal = ceil(width() / tileWidth).toInt()
        val fixedTileWidth = width() / tileCountHorizontal

        val tileCountVertical = ceil(height() / tileHeight).toInt()
        val fixedTileHeight = height() / tileCountVertical

        val startLat = min(fromLat, toLat)
        val startLon = min(fromLon, toLon)

        val startIndices = (0 until tileCountHorizontal) zip (0 until tileCountVertical)

        return startIndices.map { (latIndex, lonIndex) ->
            createBox(startLat, latIndex, fixedTileWidth, startLon, lonIndex, fixedTileHeight)
        }
    }

    private fun createBox(startLat: Double, latIndex: Int, fixedTileWidth: Double, startLon: Double, lonIndex: Int, fixedTileHeight: Double): BoundingBox {
        return BoundingBox(
                startLat + latIndex * fixedTileWidth, startLon + lonIndex * fixedTileHeight,
                startLat + (latIndex + 1) * fixedTileWidth, startLon + (lonIndex + 1) * fixedTileHeight
        )
    }

    companion object {

        fun createFixed(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): BoundingBox {
            val (fixedFromLat, fixedToLat) = getFixedLatitudes(fromLat, toLat)
            val (fixedFromLon, fixedToLon) = getFixedLongitudes(fromLon, toLon)
            return BoundingBox(fixedFromLat, fixedFromLon, fixedToLat, fixedToLon)
        }

        private fun getFixedLongitudes(fromLon: Double, toLon: Double) = if (fromLon < toLon) (fromLon to toLon) else (toLon to fromLon)
        private fun getFixedLatitudes(fromLat: Double, toLat: Double) = if (fromLat < toLat) (fromLat to toLat) else (toLat to fromLat)
    }
}