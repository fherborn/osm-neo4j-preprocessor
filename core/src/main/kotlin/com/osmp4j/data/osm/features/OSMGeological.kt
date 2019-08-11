package com.osmp4j.data.osm.features

object OSMGeological : OSMFeature<OSMGeological>("geological") {
    val MORAINE = value("moraine")
    val OUTCROP = value("outcrop")
    val PALAEONTOLOGICAL_SITE = value("palaeontological_site")
}