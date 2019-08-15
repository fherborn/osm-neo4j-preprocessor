package com.osmp4j.data.osm.elements.attributes

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.noarg.NoArg

@NoArg
data class OSMNodeRef(
        @JacksonXmlProperty(isAttribute = true)
        override val ref: Long
) : OSMRef