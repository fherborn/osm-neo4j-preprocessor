package com.osmp4j.data.osm.extensions

import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.elements.attributes.OSMTag
import com.osmp4j.data.osm.features.OSMAttribute
import com.osmp4j.data.osm.features.OSMFeature
import com.osmp4j.data.osm.features.OSMValue


fun <T> Collection<OSMTag>.contains(feature: OSMFeature<T>) = any { it.k == feature.name }
fun <T> Collection<OSMTag>.contains(feature: OSMFeature<T>, value: OSMValue<T>) = any { it.k == feature.name && it.v == value.name }
fun <T> Collection<OSMTag>.contains(value: OSMValue<T>) = any { it.v == value.name }

fun <T> Collection<OSMTag>.get(attribute: OSMAttribute<T>) = find { it.k == attribute.name }


fun <T> Collection<OSMNode>.filter(feature: OSMFeature<T>) = filter { it.isFeature(feature) }
fun <T> Collection<OSMNode>.filter(feature: OSMFeature<T>, value: OSMValue<T>) = filter { it.isFeature(feature, value) }
fun <T> Collection<OSMNode>.filter(value: OSMValue<T>) = filter { it.hasValue(value) }

fun <T> Collection<OSMNode>.map(attribute: OSMAttribute<T>) = map { it.get(attribute) }.map { it?.v }
fun <T> Collection<OSMNode>.mapNotNull(attribute: OSMAttribute<T>) = mapNotNull { it.get(attribute) }.map { it.v }
fun <T> Collection<OSMNode>.mapDistinct(attribute: OSMAttribute<T>) = mapNotNull(attribute).distinct()