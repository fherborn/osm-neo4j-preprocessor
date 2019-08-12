package com.osmp4j.geo

import kotlin.math.*

const val EARTH_RADIUS = 6378.1370

fun Double.toRadiant() = this * PI / 180

fun Double.toDegree() = this * 180 / PI

infix fun Double.distanceTo(to: Double) = abs(to - this)

data class Point(val lat: Double, val lon: Double) {
    infix fun distanceTo(to: Point): Double {

        val distanceLatRadiant = (to.lat - lat).toRadiant()
        val distanceLonRadiant = (to.lon - lon).toRadiant()

        val latFromRadiant = lat.toRadiant()
        val latToRadiant = to.lat.toRadiant()

        val a = sin(distanceLatRadiant / 2) *
                sin(distanceLatRadiant / 2) +
                sin(distanceLonRadiant / 2) *
                sin(distanceLonRadiant / 2) *
                cos(latFromRadiant) *
                cos(latToRadiant)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS * c
    }
}