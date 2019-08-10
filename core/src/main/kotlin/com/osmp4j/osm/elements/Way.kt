package com.osmp4j.osm.elements

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.noarg.NoArg
import com.osmp4j.osm.elements.attributes.NodeRef
import com.osmp4j.osm.elements.attributes.Tag

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
) : Element