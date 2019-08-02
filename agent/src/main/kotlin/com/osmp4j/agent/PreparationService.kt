package com.osmp4j.agent

import com.osmp4j.core.rabbitmq.QueueNames
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PreparationService @Autowired constructor(private val template: RabbitTemplate) {

    @RabbitListener(queues = [QueueNames.REQUEST_PREPARATION])
    fun onPreparationRequest(message: String) {
        println("Agent: Preparation Request -> $message")
        template.convertAndSend(QueueNames.RESPONSE_PREPARATION, "Preparation done")
    }

}