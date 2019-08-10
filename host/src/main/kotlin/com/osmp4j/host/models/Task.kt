package com.osmp4j.host.models

import com.osmp4j.models.BoundingBox
import com.osmp4j.models.Identifiable
import java.util.*

data class Task(val name: String, val email: String, val box: BoundingBox, override val id: UUID = UUID.randomUUID()) : Identifiable