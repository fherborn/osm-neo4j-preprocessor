package com.osmp4j.agent.services

import com.osmp4j.extensions.filterMap
import com.osmp4j.features.WayFeatureFactory
import com.osmp4j.features.core.featureRegistry
import com.osmp4j.ftp.FTPService
import com.osmp4j.messages.*
import com.osmp4j.mq.QueueNames
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DuplicatesService @Autowired constructor(private val template: RabbitTemplate, private val ftpService: FTPService) {

    private val logger = LoggerFactory.getLogger(DuplicatesService::class.java)

    @RabbitListener(queues = [QueueNames.DUPLICATES_REQUEST])
    fun onDuplicatesRequest(request: DuplicateRequest) {
        val file = ftpService.downloadAndDelete(request.fileName)

        when(request) {
            is NodesDuplicateRequest -> {
                val finalFile = file.filterMap("df-${file.name}", featureRegistry(request.nodeType)) { distinctBy { it.id } }
                ftpService.upload(finalFile.name, finalFile)
                template.convertAndSend(QueueNames.DUPLICATES_RESPONSE, NodesDuplicateResponse(finalFile.name, request.nodeType, request.task))
                finalFile.delete()
            }
            is WaysDuplicateRequest -> {
                val finalFile = file.filterMap("df-${file.name}", WayFeatureFactory) { distinctBy { it.id } }
                ftpService.upload(finalFile.name, finalFile)
                template.convertAndSend(QueueNames.DUPLICATES_RESPONSE, WaysDuplicateResponse(finalFile.name, request.task))
                finalFile.delete()
            }
        }


        logger.debug("Finished remove duplicates for ${file.name}")
        file.delete()
    }


}