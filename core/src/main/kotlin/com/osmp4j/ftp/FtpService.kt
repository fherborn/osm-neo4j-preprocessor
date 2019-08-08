package com.osmp4j.ftp

import org.apache.commons.net.ftp.FTPClient
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Service
class FtpService(private val ftpClientFactory: FTPClientFactory) {


    fun upload(path: String, data: File) {
        execute { storeFile(path, data.inputStream()) }
    }

    fun donwload(path: String, outFile: File) {
        execute { retrieveFile(path, outFile.outputStream()) }
    }

    fun execute(block: FTPClient.() -> Unit) {
        val client = ftpClientFactory.getClient()
        client.block()
        client.disconnect()
    }
}