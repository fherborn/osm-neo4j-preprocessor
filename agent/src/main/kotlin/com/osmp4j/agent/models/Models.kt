package com.osmp4j.agent.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.osmp4j.agent.noarg.NoArg

@NoArg
data class OsmFile(
        val osm: OsmRoot
)

@NoArg
@JacksonXmlRootElement(localName = "osm")
data class OsmRoot(

        @JacksonXmlProperty(isAttribute = true)
        val version: String,

        @JacksonXmlProperty(isAttribute = true)
        val generator: String,

        @JacksonXmlProperty(isAttribute = true)
        val copyright: String,

        @JacksonXmlProperty(isAttribute = true)
        val attribution: String,

        @JacksonXmlProperty(isAttribute = true)
        val license: String,

        @JacksonXmlProperty(isAttribute = false)
        val bounds: Bounds,

        @JacksonXmlElementWrapper(useWrapping = false)
        val node: Sequence<Node>,

        @JacksonXmlElementWrapper(useWrapping = false)
        val way: Sequence<Way>,

        @JacksonXmlElementWrapper(useWrapping = false)
        val relation: Sequence<Relation>
)

@NoArg
data class Bounds (

        @JacksonXmlProperty(isAttribute = true)
        val minlat: String,

        @JacksonXmlProperty(isAttribute = true)
        val minlon: String,

        @JacksonXmlProperty(isAttribute = true)
        val maxlat: String,

        @JacksonXmlProperty(isAttribute = true)
        val maxlon: String
)

@NoArg
data class Node(

        @JacksonXmlProperty(isAttribute = true)
        val lat: String,

        @JacksonXmlProperty(isAttribute = true)
        val lon: String,

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
        override val uid: String,

        @JacksonXmlElementWrapper(useWrapping = false)
        override val tag: HashMap<String, String>
): OsmEntity

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
        override val uid: String,

        @JacksonXmlElementWrapper(useWrapping = false)
        override val tag: HashMap<String, String>,

        @JacksonXmlElementWrapper(useWrapping = false)
        val member: Sequence<Member>
): OsmEntity

@NoArg
data class Member(

        @JacksonXmlProperty(isAttribute = true)
        val type: String,

        @JacksonXmlProperty(isAttribute = true)
        val role: String,

        @JacksonXmlProperty(isAttribute = true)
        override val ref: Long
): Ref

@NoArg
data class Way(

        @JacksonXmlElementWrapper(useWrapping = false)
        val nd: Sequence<NodeRef>,

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
        override val uid: String,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonIgnore
        override val tag: HashMap<String, String> = hashMapOf()
): OsmEntity

@NoArg
data class NodeRef(

        @JacksonXmlProperty(isAttribute = true)
        override val ref: Long
) : Ref


interface OsmEntity {
    val id: Long
    val visible: Boolean
    val version: Int
    val changeset: Long
    val timestamp: String
    val user: String
    val uid: String
        val tag: HashMap<String, String>
}

interface Ref {
    val ref: Long
}






