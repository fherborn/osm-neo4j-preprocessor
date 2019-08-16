package com.osmp4j.messages

import com.osmp4j.data.NodeType
import com.osmp4j.models.BoundingBox
import java.io.Serializable

interface Message : Serializable {
    val task: TaskInfo
}

enum class FileType {
    WAYS,
    NODES
}

data class PreparationRequest(val box: BoundingBox, override val task: TaskInfo) : Message

sealed class DuplicateRequest(val fileName: String) : Message
class NodesDuplicateRequest(fileName: String, val nodeType: NodeType, override val task: TaskInfo) : DuplicateRequest(fileName)
class WaysDuplicateRequest(fileName: String, override val task: TaskInfo) : DuplicateRequest(fileName)

data class PreparationResponse(val files: ResultFileNameHolder, override val task: TaskInfo) : Message

sealed class DuplicateResponse(val fileName: String) : Message
class NodesDuplicateResponse(fileName: String, val nodeType: NodeType, override val task: TaskInfo) : DuplicateResponse(fileName)
class WaysDuplicateResponse(fileName: String, override val task: TaskInfo) : DuplicateResponse(fileName)


sealed class PreparationError : Message
data class BoundingBoxToLargeError(val box: BoundingBox, override val task: TaskInfo) : PreparationError()

sealed class DuplicatesError : Message
