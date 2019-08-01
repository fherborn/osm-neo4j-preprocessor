package com.osmp4j.host

import com.osmp4j.host.config.RabbitConf
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DuplicatesService @Autowired constructor(private val template: RabbitTemplate) {

    fun removeDuplicates() {
        println("Host: Duplicates Request")
        template.convertAndSend("","Remove Duplicates")
    }

    @RabbitListener(queues = [""])
    fun onDuplicatesResponse(message: String) {
        println("Host: Duplicates Response -> $message")
    }

}