package com.osmp4j.agent

import com.osmp4j.core.rabbitmq.QueueNames
import com.osmp4j.core.rabbitmq.models.DuplicateRequest
import com.osmp4j.core.rabbitmq.models.Success
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DuplicatesService @Autowired constructor(private val template: RabbitTemplate) {

    private val logger = LoggerFactory.getLogger(DuplicatesService::class.java)

    @RabbitListener(queues = [QueueNames.REQUEST_DUPLICATES])
    fun onDuplicatesRequest(request: DuplicateRequest) {

        logger.debug("Received Duplicate_Request with ID: ${request.id}")

        template.convertAndSend(QueueNames.RESPONSE_DUPLICATES, Success("", request.id))
    }

}