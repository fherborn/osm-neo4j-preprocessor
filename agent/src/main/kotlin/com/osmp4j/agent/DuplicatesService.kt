package com.osmp4j.agent

import com.osmp4j.mq.DuplicateRequest
import com.osmp4j.mq.QueueNames
import com.osmp4j.mq.Success
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DuplicatesService @Autowired constructor(private val template: RabbitTemplate) {

    @RabbitListener(queues = [QueueNames.REQUEST_DUPLICATES])
    fun onDuplicatesRequest(request: DuplicateRequest) {
        println("Agent: Duplicates Request -> $request")
        template.convertAndSend(QueueNames.RESPONSE_DUPLICATES, Success("", request.id))
    }

}