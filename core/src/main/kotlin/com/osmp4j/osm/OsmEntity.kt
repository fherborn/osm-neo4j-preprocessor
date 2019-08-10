package com.osmp4j.osm

interface OsmEntity {
    val id: Long
    val visible: Boolean
    val version: Int
    val changeset: Long
    val timestamp: String
    val user: String
    val uid: Long
    val tag: List<Tag>?
}