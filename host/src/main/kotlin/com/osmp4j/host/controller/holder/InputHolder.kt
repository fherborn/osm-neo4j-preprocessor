package com.osmp4j.host.controller.holder

import com.osmp4j.features.core.FeatureType

data class InputHolder(
        var taskName: String = "Export",
        var fromLat: Double = 7.0862,
        var fromLon: Double = 51.0138,
        var toLat: Double = 7.7344,
        var toLon: Double = 51.3134,
        var features: List<FeatureType> = listOf()
)
