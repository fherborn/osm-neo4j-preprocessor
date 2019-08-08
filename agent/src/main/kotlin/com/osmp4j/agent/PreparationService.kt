package com.osmp4j.agent

import com.osmp4j.http.HttpService
import com.osmp4j.mq.PreparationRequest
import com.osmp4j.mq.QueueNames
import com.osmp4j.mq.Success
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PreparationService @Autowired constructor(private val template: RabbitTemplate, private val httpService: HttpService) {

    private val logger = LoggerFactory.getLogger(PreparationService::class.java)

    @RabbitListener(queues = [QueueNames.REQUEST_PREPARATION])
    fun onPreparationRequest(request: PreparationRequest) {

        logger.debug("Received request with ID: ${request.id}, BoundingBox: ${request.boundingBox}")

        //Test
        httpService.download(request.boundingBox.toQuery("node"))

        template.convertAndSend(QueueNames.RESPONSE_PREPARATION, Success("filename", request.id))
    }

}