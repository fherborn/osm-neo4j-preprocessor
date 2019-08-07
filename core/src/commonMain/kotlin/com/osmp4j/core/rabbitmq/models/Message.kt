package com.osmp4j.core.rabbitmq.models

import com.osmp4j.core.uuid.UUIDGenerator


sealed class Message(val id: String)

sealed class Request(requestId: String) : Message(requestId)
class PreparationRequest(val taskName: String, val boundingBox: BoundingBox, requestId: String = UUIDGenerator.randomUUID()) : Request(requestId)

sealed class Response(requestId: String) : Message(requestId)
class PreparationResponse(val taskName: String, val boundingBox: BoundingBox, val fileName: String, requestId: String) : Response(requestId)

sealed class Error(val message: String, requestId: String) : Message(requestId)
class UnknownError(requestId: String) : Error("Something went wrong", requestId)
