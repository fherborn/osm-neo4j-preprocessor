package com.osmp4j.host.controller

import com.osmp4j.ftp.FTPService
import com.osmp4j.host.rabbit.PreparationService
import com.osmp4j.mq.BoundingBox
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.util.*

@RestController
@RequestMapping("test")
class TestController @Autowired constructor(private val ftpService: FTPService, private val preparationService: PreparationService) {

    private val logger = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping("/ftp")
    fun testFTP() {
        val file = File("${UUID.randomUUID()}.txt")
        file.createNewFile()
        file.writeText("Hallo123")
        ftpService.upload("/tmp.txt", file)
        file.delete()

        val outFile = File("output.txt")
        ftpService.download("/tmp", outFile)
        println(outFile.readText())
        outFile.delete()
    }

    @GetMapping("/osm")
    fun testOSM() {
        logger.debug("Preparing request")
        preparationService.prepare("test", BoundingBox.createFixed(7.84754, 51.02836, 7.84938, 51.0298))

    }
}