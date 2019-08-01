package com.osmp4j.agent.config

import com.osmp4j.core.rabbitmq.QueuesNames
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitConf {

    @Bean
    fun requestPreparationQueue(): Queue {
        return Queue(QueuesNames.REQUEST_PREPARATION, false)
    }

    @Bean
    fun responsePreparationQueue(): Queue {
        return Queue(QueuesNames.RESPONSE_PREPARATION, false)
    }

    @Bean
    fun requestDuplicatesQueue(): Queue {
        return Queue(QueuesNames.REQUEST_DUPLICATES, false)
    }

    @Bean
    fun responseDuplicatesQueue(): Queue {
        return Queue(QueuesNames.RESPONSE_DUPLICATES, false)
    }

}