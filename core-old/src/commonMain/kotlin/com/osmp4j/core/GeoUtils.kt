package com.osmp4j.core

import kotlin.math.*

const val EARTH_RADIUS = 6378.1370

fun Double.toRadiant() = this * PI / 180

fun Double.toDegree() = this * 180 / PI

infix fun Double.distanceTo(to: Double) = abs(to - this)

fun distanceInKm(fromLatDegree: Double, fromLonDegree: Double, toLatDegree: Double, toLonDegree: Double): Double {

    val distanceLatRadiant = (toLatDegree - fromLatDegree).toRadiant()
    val distanceLonRadiant = (toLonDegree - fromLonDegree).toRadiant()

    val latFromRadiant = fromLatDegree.toRadiant()
    val latToRadiant = toLatDegree.toRadiant()

    val a = sin(distanceLatRadiant / 2) * sin(distanceLatRadiant / 2) + sin(distanceLonRadiant / 2) * sin(distanceLonRadiant / 2) * cos(latFromRadiant) * cos(latToRadiant)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return EARTH_RADIUS * c
}