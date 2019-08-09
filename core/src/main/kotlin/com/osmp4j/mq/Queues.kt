package com.osmp4j.mq

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class Queues {

    @Bean
    fun requestPreparationQueue(): Queue {
        return Queue(QueueNames.PREPARATION_REQUEST, false)
    }

    @Bean
    fun responsePreparationQueue(): Queue {
        return Queue(QueueNames.PREPARATION_RESPONSE, false)
    }

    @Bean
    fun errorPreparationQueue(): Queue {
        return Queue(QueueNames.PREPARATION_ERROR, false)
    }

    @Bean
    fun requestDuplicatesQueue(): Queue {
        return Queue(QueueNames.REQUEST_DUPLICATES, false)
    }

    @Bean
    fun responseDuplicatesQueue(): Queue {
        return Queue(QueueNames.RESPONSE_DUPLICATES, false)
    }

    @Bean
    fun errorDuplicatesQueue(): Queue {
        return Queue(QueueNames.ERROR_DUPLICATES, false)
    }

}