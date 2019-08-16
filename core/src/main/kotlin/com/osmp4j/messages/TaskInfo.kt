package com.osmp4j.messages

import com.osmp4j.data.NodeType
import com.osmp4j.models.BoundingBox
import com.osmp4j.models.Identifiable
import java.io.Serializable
import java.util.*

data class TaskInfo(val name: String, val box: BoundingBox, val types: List<NodeType>, override val id: UUID = UUID.randomUUID()) : Identifiable, Serializable