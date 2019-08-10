package com.osmp4j.osm.elements

import com.osmp4j.osm.elements.attributes.OSMTag

interface OSMElement {
    val id: Long
    val visible: Boolean
    val version: Int
    val changeset: Long
    val timestamp: String
    val user: String
    val uid: Long
    val tag: List<OSMTag>?
}