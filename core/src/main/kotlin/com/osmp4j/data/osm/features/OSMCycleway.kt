package com.osmp4j.data.osm.features

object OSMCycleway : OSMFeature<OSMCycleway>("cycleway") {
    val LANE = value("lane")
    val OPPOSITE = value("opposite")
    val OPPOSITE_LANE = value("opposite_lane")
    val TRACK = value("track")
    val OPPOSITE_TRACK = value("opposite_track")
    val SHARE_BUSWAY = value("share_busway")
    val OPPOSITE_SHARE_BUSWAY = value("opposite_share_busway")
    val SHARED_LANE = value("shared_lane")
}