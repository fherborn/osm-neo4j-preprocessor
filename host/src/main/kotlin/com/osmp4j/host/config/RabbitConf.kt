package com.osmp4j.host.config

import com.osmp4j.core.rabbitmq.REQUEST_DUPLICATES_QUEUE_NAME
import com.osmp4j.core.rabbitmq.REQUEST_PREPARATION_QUEUE_NAME
import com.osmp4j.core.rabbitmq.RESPONSE_DUPLICATES_QUEUE_NAME
import com.osmp4j.core.rabbitmq.RESPONSE_PREPARATION_QUEUE_NAME
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

}