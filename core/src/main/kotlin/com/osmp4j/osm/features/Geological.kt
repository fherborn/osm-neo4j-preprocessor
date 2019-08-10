package com.osmp4j.osm.features

object Geological : Feature<Geological>("geological") {
    val MORAINE = value("moraine")
    val OUTCROP = value("outcrop")
    val PALAEONTOLOGICAL_SITE = value("palaeontological_site")
}