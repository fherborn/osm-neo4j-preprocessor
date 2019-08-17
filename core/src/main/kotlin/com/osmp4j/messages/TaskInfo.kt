package com.osmp4j.messages

import com.osmp4j.data.BoundingBox
import com.osmp4j.data.Identifiable
import com.osmp4j.features.core.FeatureType
import java.io.Serializable
import java.util.*

data class TaskInfo(val name: String, val box: BoundingBox, val types: List<FeatureType>, override val id: UUID = UUID.randomUUID()) : Identifiable, Serializable