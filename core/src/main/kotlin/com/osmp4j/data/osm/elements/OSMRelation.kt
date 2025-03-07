package com.osmp4j.data.osm.elements

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.noarg.NoArg
import com.osmp4j.data.osm.elements.attributes.OSMMember
import com.osmp4j.data.osm.elements.attributes.OSMTag

@NoArg
data class OSMRelation(

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
        val member: List<OSMMember>?,

        @JacksonXmlElementWrapper(useWrapping = false)
        override val tag: List<OSMTag>?
) : OSMElement