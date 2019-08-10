package com.osmp4j.osm.file

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.osmp4j.noarg.NoArg
import com.osmp4j.osm.elements.Node
import com.osmp4j.osm.elements.Relation
import com.osmp4j.osm.elements.Way

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