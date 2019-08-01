package com.florianherborn.osmp4j.agent

import com.florianherborn.osmp4j.agent.config.RabbitConf
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PreparationService @Autowired constructor(private val template: RabbitTemplate) {

    @RabbitListener(queues = [RabbitConf.REQUEST_PREPARATION_QUEUE_NAME])
    fun onPreparationRequest(message: String) {
        println("Agent: Preparation Request -> $message")
        template.convertAndSend(RabbitConf.RESPONSE_PREPARATION_QUEUE_NAME, "Preparation done")
    }

}