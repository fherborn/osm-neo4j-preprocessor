package com.florianherborn.osmp4j.agent

import com.florianherborn.osmp4j.agent.config.RabbitConf
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DuplicatesService @Autowired constructor(private val template: RabbitTemplate) {

    @RabbitListener(queues = [RabbitConf.REQUEST_DUPLICATES_QUEUE_NAME])
    fun onDuplicatesRequest(message: String) {
        println("Agent: Duplicates Request -> $message")
        template.convertAndSend(RabbitConf.RESPONSE_DUPLICATES_QUEUE_NAME, "Duplicates removed")
    }

}