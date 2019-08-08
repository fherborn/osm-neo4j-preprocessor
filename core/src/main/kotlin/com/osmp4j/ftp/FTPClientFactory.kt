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
        logger.debug("Connect FTP Host: ${properties.host} Port: ${properties.port} User: ${properties.user}")
        client.connect(properties.host, properties.port)

        if (!FTPReply.isPositiveCompletion(client.reply)) {
            client.disconnect()
            throw IOException("Exception in connecting to FTP Server")
        }

        client.login(properties.user, properties.pass)
        return client
    }
}