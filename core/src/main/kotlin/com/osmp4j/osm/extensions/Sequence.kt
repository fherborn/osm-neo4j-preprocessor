package com.osmp4j.osm.extensions

import com.osmp4j.osm.elements.attributes.OSMTag
import com.osmp4j.osm.features.OSMFeature
import com.osmp4j.osm.features.OSMValue

fun <X> Sequence<OSMTag>.contains(feature: OSMFeature<X>) = any { it.k == feature.key }
fun <X> Sequence<OSMTag>.contains(feature: OSMFeature<X>, value: OSMValue<X>) = any { it.k == feature.key && it.v == value.name }
fun <X> Sequence<OSMTag>.contains(value: OSMValue<X>) = any { it.v == value.name }

fun <X> Sequence<OSMTag>.filter(feature: OSMFeature<X>) = filter { it.k == feature.key }
fun <X> Sequence<OSMTag>.filter(feature: OSMFeature<X>, value: OSMValue<X>) = filter { it.k == feature.key && it.v == value.name }
fun <X> Sequence<OSMTag>.filter(value: OSMValue<X>) = filter { it.v == value.name }
