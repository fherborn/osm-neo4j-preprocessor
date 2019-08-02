package com.osmp4j.core.rabbitmq

import java.util.*

actual object UUIDGenerator {
    actual fun randomUUID(): String = UUID.randomUUID().toString()
}