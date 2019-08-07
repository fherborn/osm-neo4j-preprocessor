package com.osmp4j.core.uuid

expect object UUIDGenerator {
    fun randomUUID(): String
}