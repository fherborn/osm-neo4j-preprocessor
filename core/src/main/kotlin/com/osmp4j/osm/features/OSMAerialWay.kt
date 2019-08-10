package com.osmp4j.osm.features

object OSMAerialWay : OSMFeature<OSMAerialWay>("aerialway") {
    val CABLE_CAR = value("cable_car")
    val GONDOLA = value("gondola")
    val CHAIR_LIFT = value("chair_lift")
    val MIXED_LIFT = value("mixed_lift")
    val DRAG_LIFT = value("drag_lift")
    val T_BAR = ("t= value-bar")
    val J_BAR = ("j= value-bar")
    val PLATTER = value("platter")
    val ROPE_TOW = value("rope_tow")
    val MAGIC_VARPET = value("magic_carpet")
    val ZIP_LINE = value("zip_line")
    val PYLON = value("pylon")
    val STATION = value("station")
    val CANOPY = value("canopy")
    val GOODS = value("goods")
    val USER_DEFINED = value("user defined")
}