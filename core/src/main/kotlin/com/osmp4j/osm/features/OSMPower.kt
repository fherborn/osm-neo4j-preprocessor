package com.osmp4j.osm.features

object OSMPower : OSMFeature<OSMPower>("power") {
    val PLANT = value("plant")
    val CABLE = value("cable")
    val CATENARY_MAST = value("catenary_mast")
    val COMPENSATOR = value("compensator")
    val CONNECTION = value("connection")
    val CONVERTER = value("converter")
    val GENERATOR = value("generator")
    val HELIOSTAT = value("heliostat")
    val INSULATOR = value("insulator")
    val LINE = value("line")
    val MARKER = value("marker")
    val MINOR_LINE = value("minor_line")
    val POLE = value("pole")
    val PORTAL = value("portal")
    val SUBSTATION = value("substation")
    val SWITCH = value("switch")
    val SWITCHGEAR = value("switchgear")
    val TERMINAL = value("terminal")
    val TOWER = value("tower")
    val TRANSFORMER = value("transformer")
    val USER_DEFINED = value("User Defined")

}
