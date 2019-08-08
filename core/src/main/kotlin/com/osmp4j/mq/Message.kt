package com.osmp4j.mq

import java.io.Serializable
import java.util.*

interface MQMessage : Serializable {
    val id: UUID
}

data class PreparationRequest(val taskName: String, val boundingBox: BoundingBox, override val id: UUID = UUID.randomUUID()) : MQMessage
data class DuplicateRequest(val taskName: String, val fileName: String, override val id: UUID = UUID.randomUUID()) : MQMessage

data class PreparationResponse(val fileName: String, override val id: UUID) : MQMessage
data class DuplicateResponse(val fileName: String, override val id: UUID) : MQMessage

sealed class PreparationError(val message: String, override val id: UUID) : MQMessage
class BoxToLargeError(id: UUID) : PreparationError("The bounding box is to large!", id)
