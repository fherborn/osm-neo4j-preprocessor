package com.osmp4j.core

import org.apache.commons.net.PrintCommandListener
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import java.io.*

interface FtpProperties {
    var host: String
    var user: String
    var pass: String
    var port: Int
}

class FTPClientFactory(private val properties: FtpProperties) {
    fun getClient(): FTPClient {
        val ftpClient = FTPClient()
        ftpClient.addProtocolCommandListener(PrintCommandListener(PrintWriter(System.out)))
        ftpClient.connect(properties.host, properties.port)
        val reply = ftpClient.replyCode
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect()
            throw IOException("Exception in connecting to FTP Server")
        }

        ftpClient.login(properties.user, properties.pass)

        return ftpClient
    }
}

class FtpService(private val ftpClientFactory: FTPClientFactory) {


    fun upload(path: String, data: File) {
        val client = ftpClientFactory.getClient()
        client.storeFile(path, FileInputStream(data))
        client.disconnect()
    }

    fun donwload(path: String) {
        val client: FTPClient = ftpClientFactory.getClient()
        val file = File("output.txt")
        client.retrieveFile(path, FileOutputStream(file))
        client.disconnect()
    }
}