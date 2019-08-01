package com.osmp4j.host.config

import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitConf {

    @Bean
    fun requestPreparationQueue(): Queue {
        return Queue("", false)
    }

    @Bean
    fun responsePreparationQueue(): Queue {
        return Queue("", false)
    }

    @Bean
    fun requestDuplicatesQueue(): Queue {
        return Queue("", false)
    }

    @Bean
    fun responseDuplicatesQueue(): Queue {
        return Queue("", false)
    }

}