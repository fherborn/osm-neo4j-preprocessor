package com.osmp4j.osm.elements.attributes

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.noarg.NoArg
import com.osmp4j.osm.features.Feature
import com.osmp4j.osm.features.Value

@NoArg
data class Tag(
        @JacksonXmlProperty(isAttribute = true)
        val k: String,

        @JacksonXmlProperty(isAttribute = true)
        val v: String
)











