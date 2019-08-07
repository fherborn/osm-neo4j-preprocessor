package com.osmp4j.agent

import com.osmp4j.core.rabbitmq.QueueNames
import com.osmp4j.core.rabbitmq.models.PreparationRequest
import com.osmp4j.core.rabbitmq.models.Success
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PreparationService @Autowired constructor(private val template: RabbitTemplate) {

    @RabbitListener(queues = [QueueNames.REQUEST_PREPARATION])
    fun onPreparationRequest(request: PreparationRequest) {
        println("Agent: Preparation Request -> $request")
        template.convertAndSend(QueueNames.RESPONSE_PREPARATION, Success("filename", request.id))
    }

}