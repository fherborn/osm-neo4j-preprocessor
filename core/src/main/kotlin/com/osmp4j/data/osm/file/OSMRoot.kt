package com.osmp4j.data.osm.file

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.osmp4j.noarg.NoArg
import com.osmp4j.data.osm.elements.OSMNode
import com.osmp4j.data.osm.elements.OSMRelation
import com.osmp4j.data.osm.elements.OSMWay

@NoArg
@JacksonXmlRootElement(localName = "osm")
class OSMRoot(

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
        val bounds: OSMBounds,

        @JacksonXmlElementWrapper(useWrapping = false)
        val node: List<OSMNode>?,

        @JacksonXmlElementWrapper(useWrapping = false)
        val way: List<OSMWay>?,

        @JacksonXmlElementWrapper(useWrapping = false)
        val relation: List<OSMRelation>?
)