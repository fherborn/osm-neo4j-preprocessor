package com.osmp4j.host

import org.apache.commons.net.PrintCommandListener
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.io.*


@Component
@ConfigurationProperties("osmp4j.ftpd")
class FtpProperties {
    @Value("\${osmp4j.ftpd.host}")
    lateinit var ftpdHost: String

    @Value("\${osmp4j.ftpd.user.name}")
    lateinit var ftpdUserName: String

    @Value("\${osmp4j.ftpd.user.pass}")
    lateinit var ftpdUserPass: String
}

class FTPClientFactory(
        private val host: String,
        private val port: Int,
        private val user: String,
        private val password: String
) {
    fun getClient(): FTPClient {
        val ftpClient = FTPClient()
        ftpClient.addProtocolCommandListener(PrintCommandListener(PrintWriter(System.out)))
        ftpClient.connect(host, port)
        val reply = ftpClient.replyCode
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect()
            throw IOException("Exception in connecting to FTP Server")
        }

        ftpClient.login(user, password)

        return ftpClient
    }
}

@Configuration
class FtpConfiguration @Autowired constructor(private val ftpProperties: FtpProperties) {

    @Bean
    fun ftpClientFactory(props: FtpProperties) = FTPClientFactory(props.ftpdHost, 21, props.ftpdUserName, props.ftpdUserPass)
}

@Service
class FtpService @Autowired constructor(private val ftpClientFactory: FTPClientFactory) {


    fun upload() {
        val fileName = "data.txt"
        val file = File(fileName)

        val isNewFileCreated :Boolean = file.createNewFile()

        if(isNewFileCreated){
            file.writeText("Dies ist ein test Text")
        }

        val client = ftpClientFactory.getClient()

        client.storeFile("/test.txt", FileInputStream(file))

        client.disconnect()

        donwload()
    }

    fun donwload() {
        val client : FTPClient = ftpClientFactory.getClient()

        val file = File("output.txt")
        client.retrieveFile("/test.txt", FileOutputStream(file))

        client.disconnect()


        println("Retrived file ${file.readText()}")
    }
}

