package com.osmp4j.osm

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.osmp4j.noarg.NoArg

@NoArg
class NodeRef(

        @JacksonXmlProperty(isAttribute = true)
        override val ref: Long
) : Ref