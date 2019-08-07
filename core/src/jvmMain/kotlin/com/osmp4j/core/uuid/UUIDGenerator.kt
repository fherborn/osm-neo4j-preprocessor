package com.osmp4j.core.uuid

import java.util.*

actual object UUIDGenerator {
    actual fun randomUUID(): String = UUID.randomUUID().toString()
}