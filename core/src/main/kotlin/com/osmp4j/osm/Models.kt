package com.osmp4j.osm

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.osmp4j.noarg.NoArg

@NoArg
@JacksonXmlRootElement(localName = "osm")
class OsmRoot(

        @JacksonXmlProperty(isAttribute = true)
        var version: Double,

        @JacksonXmlProperty(isAttribute = true)
        var generator: String,

        @JacksonXmlProperty(isAttribute = true)
        var copyright: String,

        @JacksonXmlProperty(isAttribute = true)
        var attribution: String,

        @JacksonXmlProperty(isAttribute = true)
        var license: String,

        @JacksonXmlProperty(isAttribute = false)
        val bounds: Bounds,

        @JacksonXmlElementWrapper(useWrapping = false)
        val node: List<Node>?,

        @JacksonXmlElementWrapper(useWrapping = false)
        val way: List<Way>?,

        @JacksonXmlElementWrapper(useWrapping = false)
        val relation: List<Relation>?
)

@NoArg
data class Bounds(

        @JacksonXmlProperty(isAttribute = true)
        val minlat: Double,

        @JacksonXmlProperty(isAttribute = true)
        val minlon: Double,

        @JacksonXmlProperty(isAttribute = true)
        val maxlat: Double,

        @JacksonXmlProperty(isAttribute = true)
        val maxlon: Double
)

@NoArg
data class Node(

        @JacksonXmlProperty(isAttribute = true)
        override val id: Long,

        @JacksonXmlProperty(isAttribute = true)
        override val visible: Boolean,

        @JacksonXmlProperty(isAttribute = true)
        override val version: Int,

        @JacksonXmlProperty(isAttribute = true)
        override val changeset: Long,

        @JacksonXmlProperty(isAttribute = true)
        override val timestamp: String,

        @JacksonXmlProperty(isAttribute = true)
        override val user: String,

        @JacksonXmlProperty(isAttribute = true)
        override val uid: Long,

        @JacksonXmlProperty(isAttribute = true)
        val lat: Double,

        @JacksonXmlProperty(isAttribute = true)
        val lon: Double,

        @JacksonXmlElementWrapper(useWrapping = false)
        override val tag: List<Tag>?
) : OsmEntity

@NoArg
data class Relation(

        @JacksonXmlProperty(isAttribute = true)
        override val id: Long,

        @JacksonXmlProperty(isAttribute = true)
        override val visible: Boolean,

        @JacksonXmlProperty(isAttribute = true)
        override val version: Int,

        @JacksonXmlProperty(isAttribute = true)
        override val changeset: Long,

        @JacksonXmlProperty(isAttribute = true)
        override val timestamp: String,

        @JacksonXmlProperty(isAttribute = true)
        override val user: String,

        @JacksonXmlProperty(isAttribute = true)
        override val uid: Long,

        @JacksonXmlElementWrapper(useWrapping = false)
        val member: List<Member>?,

        @JacksonXmlElementWrapper(useWrapping = false)
        override val tag: List<Tag>?
) : OsmEntity

@NoArg
class Member(

        @JacksonXmlProperty(isAttribute = true)
        val type: String,

        @JacksonXmlProperty(isAttribute = true)
        override val ref: Long,

        @JacksonXmlProperty(isAttribute = true)
        val role: String
) : Ref

@NoArg
data class Way(

        @JacksonXmlProperty(isAttribute = true)
        override val id: Long,

        @JacksonXmlProperty(isAttribute = true)
        override val visible: Boolean,

        @JacksonXmlProperty(isAttribute = true)
        override val version: Int,

        @JacksonXmlProperty(isAttribute = true)
        override val changeset: Long,

        @JacksonXmlProperty(isAttribute = true)
        override val timestamp: String,

        @JacksonXmlProperty(isAttribute = true)
        override val user: String,

        @JacksonXmlProperty(isAttribute = true)
        override val uid: Long,

        @JacksonXmlElementWrapper(useWrapping = false)
        val nd: List<NodeRef>? = listOf(),

        @JacksonXmlElementWrapper(useWrapping = false)
        override val tag: List<Tag>?
) : OsmEntity

@NoArg
class NodeRef(

        @JacksonXmlProperty(isAttribute = true)
        override val ref: Long
) : Ref

@NoArg
data class Tag(
        @JacksonXmlProperty(isAttribute = true)
        val k: String,

        @JacksonXmlProperty(isAttribute = true)
        val v: String
)

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

interface Ref {
    val ref: Long
}






