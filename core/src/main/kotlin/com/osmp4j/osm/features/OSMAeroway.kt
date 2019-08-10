package com.osmp4j.osm.features

object OSMAeroway : OSMFeature<OSMAeroway>("aeroway") {
    val AERODROME = value("aerodrome")
    val APRON = value("apron")
    val GATE = value("gate")
    val HANGAR = value("hangar")
    val HELIPAD = value("helipad")
    val HELIPORT = value("heliport")
    val NAVIGATIONAID = value("navigationaid")
    val RUNWAY = value("runway")
    val SPACEPORT = value("spaceport")
    val TAXIWAY = value("taxiway")
    val TERMINAL = value("terminal")
    val WINDSOCK = value("windsock")
}