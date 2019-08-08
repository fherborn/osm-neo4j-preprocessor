package com.osmp4j.mq

import java.io.Serializable
import java.util.*

sealed class Message(val id: UUID) : Serializable
sealed class Request(val taskName: String, id: UUID) : Message(id)


class PreparationRequest(taskName: String, val boundingBox: BoundingBox, id: UUID = UUID.randomUUID()) : Request(taskName, id)
class DuplicateRequest(taskName: String, val fileName: String, id: UUID = UUID.randomUUID()) : Request(taskName, id)

sealed class Response(id: UUID) : Message(id)
class Success(val fileName: String, id: UUID) : Response(id)
class Error(val message: String, id: UUID) : Response(id)
