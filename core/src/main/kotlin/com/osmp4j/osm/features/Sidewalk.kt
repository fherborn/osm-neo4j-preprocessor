package com.osmp4j.osm.features

object Sidewalk : Feature<Sidewalk>("sidewalk") {
    val BOTH = value("both")
    val RIGHT = value("right")
    val LEFT = value("left")
    val NONE = value("none")
    val NO = value("no")
    val YES = value("yes")
    val SEPARATE = value("separate")
}