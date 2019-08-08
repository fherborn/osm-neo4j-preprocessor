package com.osmp4j.host

import com.osmp4j.core.rabbitmq.FtpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File


@RestController
@RequestMapping("test")
class TestController @Autowired constructor(private val ftpService: FtpService) {

    @GetMapping("/ftp")
    fun testFtp() {

        val file = File("test.txt")
        file.createNewFile()
        file.writeText("bla bla")

        ftpService.upload("/tmp", file)
    }

}