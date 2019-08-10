package com.osmp4j.osm.features

object Service : Feature<Service>("service") {
    val CROSSOVER = value("crossover")
    val SIDING = value("siding")
    val SPUR = value("spur")
    val YARD = value("yard")
}
