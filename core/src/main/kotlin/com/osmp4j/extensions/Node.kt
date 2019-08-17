package com.osmp4j.extensions

import com.osmp4j.data.Node
import com.osmp4j.geo.Point

infix fun Node.distanceTo(other: Node) = Point(lat, lon) distanceTo Point(other.lat, other.lon)