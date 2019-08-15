package com.osmp4j.agent.services

import com.osmp4j.data.Node
import com.osmp4j.data.Way
import com.osmp4j.data.filterMap
import com.osmp4j.ftp.FTPService
import com.osmp4j.messages.DuplicateRequest
import com.osmp4j.messages.DuplicateResponse
import com.osmp4j.messages.FileType
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
        val (fileName, fileType, task) = request

        logger.debug("Remove duplicates for type $fileType - $fileName")

        // TODO delete ftp files
        val file = ftpService.downloadAndDelete(fileName)

        val finalFile = when (fileType) {
            FileType.NODES -> file.filterMap("df-${file.name}", Node) { distinctBy { it.id } }
            FileType.WAYS -> file.filterMap("df-${file.name}", Way) { distinctBy { it.id } }
        }


        ftpService.upload(finalFile.name, finalFile)

        file.delete()
        finalFile.delete()

        template.convertAndSend(QueueNames.DUPLICATES_RESPONSE, DuplicateResponse(finalFile.name, fileType, task))

        logger.debug("Finished remove duplicates for type $fileType - $fileName")
    }


}