package com.osmp4j.data.osm.elements

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.data.osm.elements.attributes.OSMTag
import com.osmp4j.noarg.NoArg

@NoArg
data class OSMNode(

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
        override val tag: List<OSMTag>?
) : OSMElement