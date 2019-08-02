package com.osmp4j.core.rabbitmq

expect object UUIDGenerator {
    fun randomUUID(): String
}