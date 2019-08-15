package com.osmp4j.messages

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
data class DuplicateRequest(val fileName: String, val fileType: FileType, override val task: TaskInfo) : Message

data class PreparationResponse(val files: ResultFileNameHolder, override val task: TaskInfo) : Message
data class DuplicateResponse(val fileName: String, val fileType: FileType, override val task: TaskInfo) : Message


sealed class PreparationError : Message
data class BoundingBoxToLargeError(val box: BoundingBox, override val task: TaskInfo) : PreparationError()

sealed class DuplicatesError : Message
