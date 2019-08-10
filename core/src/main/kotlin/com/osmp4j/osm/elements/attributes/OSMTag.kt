package com.osmp4j.osm.elements.attributes

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.noarg.NoArg

@NoArg
data class OSMTag(
        @JacksonXmlProperty(isAttribute = true)
        val k: String,

        @JacksonXmlProperty(isAttribute = true)
        val v: String
)











