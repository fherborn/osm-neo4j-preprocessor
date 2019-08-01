package com.osmp4j.core.rabbitmq

object Queues {
    const val REQUEST_PREPARATION_QUEUE_NAME = "osmp4j.preparation.request"
    const val RESPONSE_PREPARATION_QUEUE_NAME = "osmp4j.preparation.response"
    const val REQUEST_DUPLICATES_QUEUE_NAME = "osmp4j.duplicates.request"
    const val RESPONSE_DUPLICATES_QUEUE_NAME = "osmp4j.duplicates.response"
}