package com.osmp4j.ftp

import org.apache.commons.net.PrintCommandListener
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.PrintWriter

@Service
@EnableConfigurationProperties(FtpProperties::class)
class FTPClientFactory(private val properties: FtpProperties) {

    private val logger = LoggerFactory.getLogger(FTPClientFactory::class.java)

    fun getClient(): FTPClient {
        val client = FTPClient()
        client.addProtocolCommandListener(PrintCommandListener(PrintWriter(System.out)))
        logger.debug("Connect FTPService Host: ${properties.host} Port: ${properties.port}")
        client.connect(properties.host, properties.port)
        client.enterLocalPassiveMode()

        if (!FTPReply.isPositiveCompletion(client.replyCode)) {
            client.disconnect()
            throw IOException("Exception in connecting to FTPService Server")
        }
        logger.debug("Login FTPService: User: ${properties.user} Pass: ${properties.pass}")

        client.login(properties.user, properties.pass)
        return client
    }
}