package com.osmp4j.messages

import com.osmp4j.data.Node
import com.osmp4j.features.core.FeatureFactory
import com.osmp4j.features.core.FeatureType
import java.io.File
import java.io.Serializable

data class ResultFileNameHolder(val nodeFileNames: Map<FeatureType, String>, val waysFileName: String) : Serializable
data class ResultFileHolder(val nodesFiles: List<Pair<FeatureType, File>>, val waysFile: File)
data class ResultFeatureHolder(val nodeFileNames: List<Pair<FeatureFactory<Node>, String>>, val waysFileName: String, val task: TaskInfo)