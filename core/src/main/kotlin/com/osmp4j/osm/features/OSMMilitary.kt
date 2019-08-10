package com.osmp4j.osm.features

object OSMMilitary : OSMFeature<OSMMilitary>("military") {
    val AIRFIELD = value("airfield")
    val BUNKER = value("bunker")
    val BARRACKS = value("barracks")
    val CHECKPOINT = value("checkpoint")
    val DANGER_AREA = value("danger_area")
    val NAVAL_BASE = value("naval_base")
    val NUCLEAR_EXPLOSION_SITE = value("nuclear_explosion_site")
    val OFFICE = value("office")
    val RANGE = value("range")
    val TRAINING_AREA = value("training_area")
    val TRENCH = value("trench")
}