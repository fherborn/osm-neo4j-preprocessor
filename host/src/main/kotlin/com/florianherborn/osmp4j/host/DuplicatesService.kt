package com.florianherborn.osmp4j.host

import com.florianherborn.osmp4j.host.config.RabbitConf
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DuplicatesService @Autowired constructor(private val template: RabbitTemplate) {

    fun removeDuplicates() {
        template.convertAndSend(RabbitConf.REQUEST_DUPLICATES_QUEUE_NAME,"Remove Duplicates")
    }

    @RabbitListener(queues = [RabbitConf.RESPONSE_DUPLICATES_QUEUE_NAME])
    fun onDuplicatesResponse(message: String) {
        println("Host: Duplicates Response -> $message")
    }

}