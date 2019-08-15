package com.osmp4j.messages

import java.io.Serializable

data class ResultFileHolder(val nodesFileName: String, val waysFileName: String) : Serializable