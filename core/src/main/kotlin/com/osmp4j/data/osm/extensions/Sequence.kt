package com.osmp4j.data.osm.extensions

import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.elements.attributes.OSMTag
import com.osmp4j.data.osm.features.OSMAttribute
import com.osmp4j.data.osm.features.OSMFeature
import com.osmp4j.data.osm.features.OSMValue

//TODO SEQUENCE DESERIALIZER and only operate on sequences. Remove collection methods

fun <T> Sequence<OSMTag>.contains(feature: OSMFeature<T>) = any { it.k == feature.name }
fun <T> Sequence<OSMTag>.contains(feature: OSMFeature<T>, value: OSMValue<T>) = any { it.k == feature.name && it.v == value.name }
fun <T> Sequence<OSMTag>.contains(value: OSMValue<T>) = any { it.v == value.name }

fun <T> Collection<OSMTag>.contains(feature: OSMFeature<T>) = any { it.k == feature.name }
fun <T> Collection<OSMTag>.contains(feature: OSMFeature<T>, value: OSMValue<T>) = any { it.k == feature.name && it.v == value.name }
fun <T> Collection<OSMTag>.contains(value: OSMValue<T>) = any { it.v == value.name }

fun <T> Sequence<OSMTag>.get(attribute: OSMAttribute<T>) = find { it.k == attribute.name }
fun <T> Collection<OSMTag>.get(attribute: OSMAttribute<T>) = find { it.k == attribute.name }

fun <T> OSMNode.isFeature(feature: OSMFeature<T>) = tag?.contains(feature) ?: false
fun <T> OSMNode.isFeature(feature: OSMFeature<T>, value: OSMValue<T>) = tag?.contains(feature, value) ?: false
fun <T> OSMNode.hasValue(value: OSMValue<T>) = tag?.contains(value) ?: false

fun <T> OSMNode.get(attribute: OSMAttribute<T>) = tag?.get(attribute)

fun <T> Sequence<OSMNode>.filter(feature: OSMFeature<T>) = filter { it.isFeature(feature) }
fun <T> Sequence<OSMNode>.filter(feature: OSMFeature<T>, value: OSMValue<T>) = filter { it.isFeature(feature, value) }
fun <T> Sequence<OSMNode>.filter(value: OSMValue<T>) = filter { it.hasValue(value) }

fun <T> Collection<OSMNode>.filter(feature: OSMFeature<T>) = filter { it.isFeature(feature) }
fun <T> Collection<OSMNode>.filter(feature: OSMFeature<T>, value: OSMValue<T>) = filter { it.isFeature(feature, value) }
fun <T> Collection<OSMNode>.filter(value: OSMValue<T>) = filter { it.hasValue(value) }

fun <T> Collection<OSMNode>.map(attribute: OSMAttribute<T>) = map { it.get(attribute) }.map { it?.v }
fun <T> Collection<OSMNode>.mapNotNull(attribute: OSMAttribute<T>) = mapNotNull { it.get(attribute) }.map { it.v }
fun <T> Collection<OSMNode>.mapDistinct(attribute: OSMAttribute<T>) = mapNotNull(attribute).distinct()
