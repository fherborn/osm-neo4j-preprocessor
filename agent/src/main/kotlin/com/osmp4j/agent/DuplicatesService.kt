package com.osmp4j.agent

import com.osmp4j.agent.config.RabbitConf
import com.osmp4j.core.rabbitmq.QueuesNames
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DuplicatesService @Autowired constructor(private val template: RabbitTemplate) {

    @RabbitListener(queues = [QueuesNames.REQUEST_DUPLICATES])
    fun onDuplicatesRequest(message: String) {
        println("Agent: Duplicates Request -> $message")
        template.convertAndSend(QueuesNames.RESPONSE_DUPLICATES, "Duplicates removed")
    }

}