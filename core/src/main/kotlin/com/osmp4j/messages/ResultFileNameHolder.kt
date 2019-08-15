package com.osmp4j.messages

import java.io.File
import java.io.Serializable

data class ResultFileNameHolder(val nodesFileName: String, val waysFileName: String) : Serializable

data class ResultFileHolder(val nodesFile: File, val waysFile: File)