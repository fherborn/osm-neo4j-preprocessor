package com.osmp4j.data.osm.extensions

import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.features.OSMAttribute
import com.osmp4j.data.osm.features.OSMFeature
import com.osmp4j.data.osm.features.OSMValue


fun <T> OSMNode.isFeature(feature: OSMFeature<T>) = tag?.contains(feature) ?: false
fun <T> OSMNode.isFeature(feature: OSMFeature<T>, value: OSMValue<T>) = tag?.contains(feature, value) ?: false
fun <T> OSMNode.hasValue(value: OSMValue<T>) = tag?.contains(value) ?: false

fun <T> OSMNode.get(attribute: OSMAttribute<T>) = tag?.get(attribute)