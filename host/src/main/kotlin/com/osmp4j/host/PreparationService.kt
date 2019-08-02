package com.osmp4j.host

import com.osmp4j.core.rabbitmq.QueueNames
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PreparationService @Autowired constructor(private val template: RabbitTemplate) {

    fun prepareBoundingBox() {
        println("Host: Preparate Request")
        template.convertAndSend(QueueNames.REQUEST_PREPARATION, "Prepare Bounding Box")
    }

    @RabbitListener(queues = [QueueNames.RESPONSE_PREPARATION])
    fun onPreparationResponse(message: String) {
        println("Host: Preparation Response -> $message")
    }

}