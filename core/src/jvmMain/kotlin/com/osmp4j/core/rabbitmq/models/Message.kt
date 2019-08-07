package com.osmp4j.core.rabbitmq.models

import com.osmp4j.core.uuid.UUIDGenerator
import java.io.Serializable

sealed class Message(val id: String) : Serializable
sealed class Request(val taskName: String, id: String) : Message(id)


class PreparationRequest(taskName: String, val boundingBox: BoundingBox, id: String = UUIDGenerator.randomUUID()) : Request(taskName, id)
class DuplicateRequest(taskName: String, val fileName: String, id: String = UUIDGenerator.randomUUID()) : Request(taskName, id)

sealed class Response(id: String) : Message(id)
class Success(val fileName: String, id: String) : Response(id)
class Error(val message: String, id: String) : Response(id)
