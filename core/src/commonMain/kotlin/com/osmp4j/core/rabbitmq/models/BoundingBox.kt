package com.osmp4j.core.rabbitmq.models

data class BoundingBox(var fromLat: Double, var toLat: Double, var fromLon: Double, var toLon: Double) {

    companion object {
        fun create(fromLat: Double, toLat: Double, fromLon: Double, toLon: Double): BoundingBox {
            val (fixedFromLat, fixedToLat) = extractFixedLatitudes(fromLat, toLat)
            val (fixedFromLon, fixedToLon) = getFixedLongitudes(fromLon, toLon)
            return BoundingBox(fixedFromLat, fixedToLat, fixedFromLon, fixedToLon)
        }
        private fun getFixedLongitudes(fromLon: Double, toLon: Double) = if (fromLon < toLon) (fromLon to toLon) else (toLon to fromLon)
        private fun extractFixedLatitudes(fromLat: Double, toLat: Double) = if (fromLat < toLat) (fromLat to toLat) else (toLat to fromLat)
    }

}