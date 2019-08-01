package com.florianherborn.osmp4j.agent.config

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitConf {


    @Bean
    fun requestPreparationQueue(): Queue {
        return Queue(REQUEST_PREPARATION_QUEUE_NAME, false)
    }

    @Bean
    fun responsePreparationQueue(): Queue {
        return Queue(RESPONSE_PREPARATION_QUEUE_NAME, false)
    }

    @Bean
    fun requestDuplicatesQueue(): Queue {
        return Queue(REQUEST_DUPLICATES_QUEUE_NAME, false)
    }

    @Bean
    fun responseDuplicatesQueue(): Queue {
        return Queue(RESPONSE_DUPLICATES_QUEUE_NAME, false)
    }


    companion object {
        const val REQUEST_PREPARATION_QUEUE_NAME = "osmp4j.preparation.request"
        const val RESPONSE_PREPARATION_QUEUE_NAME = "osmp4j.preparation.response"
        const val REQUEST_DUPLICATES_QUEUE_NAME = "osmp4j.duplicates.request"
        const val RESPONSE_DUPLICATES_QUEUE_NAME = "osmp4j.duplicates.response"
    }
}