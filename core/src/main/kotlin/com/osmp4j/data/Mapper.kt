package com.osmp4j.data


enum class NodeType {
    NODE,
    HIGHWAY
}


fun getConverter(type: NodeType): CSVNodeConverter<out Node> = when (type) {
    NodeType.NODE -> NodeConverter
    NodeType.HIGHWAY -> HighwayConverter
}