package com.osmp4j.data.osm.features

object OSMSidewalk : OSMFeature<OSMSidewalk>("sidewalk") {
    val BOTH = value("both")
    val RIGHT = value("right")
    val LEFT = value("left")
    val NONE = value("none")
    val NO = value("no")
    val YES = value("yes")
    val SEPARATE = value("separate")
}