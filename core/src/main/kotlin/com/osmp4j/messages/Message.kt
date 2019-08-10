package com.osmp4j.messages

import com.osmp4j.models.BoundingBox
import com.osmp4j.models.Identifiable
import java.io.Serializable
import java.util.*

interface Message : Identifiable, Serializable

data class PreparationRequest(val boundingBox: BoundingBox, override val id: UUID = UUID.randomUUID()) : Message
data class DuplicateRequest(val fileName: String, override val id: UUID = UUID.randomUUID()) : Message

data class PreparationResponse(val fileName: String, override val id: UUID) : Message
data class DuplicateResponse(val fileName: String, override val id: UUID) : Message

sealed class PreparationError(override val id: UUID) : Message
class BoundingBoxToLargeError(val boundingBox: BoundingBox, id: UUID) : PreparationError(id)
