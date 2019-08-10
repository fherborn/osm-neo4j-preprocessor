package com.osmp4j.osm.elements.attributes

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.noarg.NoArg

@NoArg
class OSMMember(

        @JacksonXmlProperty(isAttribute = true)
        val type: String,

        @JacksonXmlProperty(isAttribute = true)
        override val ref: Long,

        @JacksonXmlProperty(isAttribute = true)
        val role: String
) : OSMRef