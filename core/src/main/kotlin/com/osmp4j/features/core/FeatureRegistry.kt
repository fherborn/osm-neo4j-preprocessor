package com.osmp4j.features.core

import com.osmp4j.data.Node
import com.osmp4j.features.HighwayFeatureFactory
import com.osmp4j.features.NodeFeatureFactory


enum class FeatureType {
    NODE,
    HIGHWAY
}


fun featureRegistry(type: FeatureType): BaseNodeFeatureFactory<out Node> = when (type) {
    FeatureType.NODE -> NodeFeatureFactory
    FeatureType.HIGHWAY -> HighwayFeatureFactory
}