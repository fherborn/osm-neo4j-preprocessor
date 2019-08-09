package com.osmp4j.host

import com.osmp4j.models.Identifiable
import com.osmp4j.mq.BoundingBox
import java.util.*

data class Task(val name: String, val email: String, val box: BoundingBox, override val id: UUID = UUID.randomUUID()) : Identifiable