package com.osmp4j.data.osm.file

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.noarg.NoArg

@NoArg
data class OSMBounds(

        @JacksonXmlProperty(isAttribute = true)
        val minlat: Double,

        @JacksonXmlProperty(isAttribute = true)
        val minlon: Double,

        @JacksonXmlProperty(isAttribute = true)
        val maxlat: Double,

        @JacksonXmlProperty(isAttribute = true)
        val maxlon: Double
)