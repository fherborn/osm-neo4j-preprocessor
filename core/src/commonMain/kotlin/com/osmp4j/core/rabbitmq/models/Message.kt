package com.osmp4j.core.rabbitmq.models

import com.osmp4j.core.uuid.UUIDGenerator



sealed class Message(val id: String)

sealed class Request(requestId: String) : Message(requestId)
class PreparationRequest(val taskName: String, val boundingBox: BoundingBox, requestId: String = UUIDGenerator.randomUUID()) : Request(requestId)
class DuplicateRequest(val taskName: String, val fileName: String, requestId: String = UUIDGenerator.randomUUID()) : Request(requestId)

sealed class Response(requestId: String) : Message(requestId)
class Success(val fileName: String, requestId: String) : Response(requestId)
class Error(val message: String, requestId: String) : Response(requestId)
