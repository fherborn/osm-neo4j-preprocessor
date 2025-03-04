package com.osmp4j.data.osm.features

object OSMPlace : OSMFeature<OSMPlace>("place") {
    val COUNTRY = value("country")
    val STATE = value("state")
    val REGION = value("region")
    val PROVINCE = value("province")
    val DISTRICT = value("district")
    val COUNTY = value("county")
    val MUNICIPALITY = value("municipality")
    val CIVIL_PARISH = value("civil_parish")
    val CITY = value("city")
    val BOROUGH = value("borough")
    val SUBURB = value("suburb")
    val QUARTER = value("quarter")
    val NEIGHBOURHOOD = value("neighbourhood")
    val CITY_BLOCK = value("city_block")
    val PLOT = value("plot")
    val TOWN = value("town")
    val VILLAGE = value("village")
    val HAMLET = value("hamlet")
    val ISOLATED_DWELLING = value("isolated_dwelling")
    val FARM = value("farm")
    val ALLOTMENTS = value("allotments")
    val CONTINENT = value("continent")
    val ARCHIPELAGO = value("archipelago")
    val ISLAND = value("island")
    val ISLET = value("islet")
    val SQUARE = value("square")
    val LOCALITY = value("locality")
    val SEA = value("sea")
    val OCEAN = value("ocean")
}
