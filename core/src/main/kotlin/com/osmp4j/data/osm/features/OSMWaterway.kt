package com.osmp4j.data.osm.features

object OSMWaterway : OSMFeature<OSMWaterway>("waterway") {
    val RIVER = value("river")
    val RIVERBANK = value("riverbank")
    val STREAM = value("stream")
    val TIDAL_CHANNEL = value("tidal_channel")
    val CANAL = value("canal")
    val DRAIN = value("drain")
    val DITCH = value("ditch")
    val PRESSURISED = value("pressurised")
    val FAIRWAY = value("fairway")
    val DOCK = value("dock")
    val BOATYARD = value("boatyard")
    val DAM = value("dam")
    val WEIR = value("weir")
    val WATERFALL = value("waterfall")
    val LOCK_GATE = value("lock_gate")
    val TURNING_POINT = value("turning_point")
    val WATER_POINT = value("water_point")
    val FUEL = value("fuel")
}
