package com.osmp4j.messages

import com.osmp4j.data.NodeType
import java.io.File
import java.io.Serializable

data class ResultFileNameHolder(val nodeFileNames: Map<NodeType, String>, val waysFileName: String) : Serializable

data class ResultFileHolder(val nodesFile: List<Pair<NodeType, File>>, val waysFile: File)