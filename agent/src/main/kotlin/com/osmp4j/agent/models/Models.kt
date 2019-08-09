package com.osmp4j.agent.models


data class OsmFile(
        val osmRoot: OsmRoot
)

data class OsmRoot(
        val version: String,
        val generator: String,
        val copyright: String,
        val attribution: String,
        val license: String,
        val bounds: Bounds,
        val nodes: Sequence<Node>,
        val ways: Sequence<Way>,
        val relations: Sequence<Relation>
)
data class Bounds (
        val minlat: String,
        val minlon: String,
        val maxlat: String,
        val maxlon: String
)

data class Node(
        val lat: String,
        val lon: String,
        override val id: Long,
        override val visible: Boolean,
        override val version: Int,
        override val changeset: Long,
        override val timestamp: String,
        override val user: String,
        override val uid: String,
        override val tags: HashMap<String, String>
): OsmEntity

data class Relation(
        override val id: Long,
        override val visible: Boolean,
        override val version: Int,
        override val changeset: Long,
        override val timestamp: String,
        override val user: String,
        override val uid: String,
        override val tags: HashMap<String, String>,
        val members: Sequence<Member>
): OsmEntity

data class Member(
        val type: String,
        val role: String,
        override val ref: Long
): Ref

data class Way(
        val nd: Sequence<NodeRef>,
        override val id: Long,
        override val visible: Boolean,
        override val version: Int,
        override val changeset: Long,
        override val timestamp: String,
        override val user: String,
        override val uid: String,
        override val tags: HashMap<String, String>
): OsmEntity

data class NodeRef(override val ref: Long): Ref


interface OsmEntity {
    val id: Long
    val visible: Boolean
    val version: Int
    val changeset: Long
    val timestamp: String
    val user: String
    val uid: String
    val tags: HashMap<String, String>
}

interface Ref {
    val ref: Long
}






