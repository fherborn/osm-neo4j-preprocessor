package com.florianherborn.osmp4j.host

import com.florianherborn.osmp4j.host.config.RabbitConf
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PreparationService @Autowired constructor(private val template: RabbitTemplate) {

    fun prepateBoundingBox() {
        template.convertAndSend(RabbitConf.REQUEST_PREPARATION_QUEUE_NAME,"Prepare Bounding Box")
    }

    @RabbitListener(queues = [RabbitConf.RESPONSE_PREPARATION_QUEUE_NAME])
    fun onPreparationResponse(message: String) {
        println("Host: Preparation Response -> $message")
    }

}