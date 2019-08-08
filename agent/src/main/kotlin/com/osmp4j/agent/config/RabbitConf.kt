package com.osmp4j.agent.config

import com.osmp4j.mq.QueueNames
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitConf {

    @Bean
    fun requestPreparationQueue(): Queue {
        return Queue(QueueNames.REQUEST_PREPARATION, false)
    }

    @Bean
    fun responsePreparationQueue(): Queue {
        return Queue(QueueNames.RESPONSE_PREPARATION, false)
    }

    @Bean
    fun requestDuplicatesQueue(): Queue {
        return Queue(QueueNames.REQUEST_DUPLICATES, false)
    }

    @Bean
    fun responseDuplicatesQueue(): Queue {
        return Queue(QueueNames.RESPONSE_DUPLICATES, false)
    }

}