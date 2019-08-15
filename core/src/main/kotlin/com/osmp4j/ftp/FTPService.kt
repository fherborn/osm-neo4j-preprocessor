package com.osmp4j.ftp

import org.apache.commons.net.ftp.FTPClient
import org.springframework.stereotype.Service
import java.io.File

fun FTPClient.storeFile(remote: String, file: File) = storeFile(remote, file.inputStream())
fun FTPClient.retrieveFile(remote: String, file: File) = retrieveFile(remote, file.outputStream())


@Service
class FTPService(private val ftpClientFactory: FTPClientFactory) {

    fun execute(block: FTPClient.() -> Unit) {
        val client = ftpClientFactory.getClient()
        client.block()
        client.disconnect()
    }

    fun mkDir(dir: String) = execute { makeDirectory(dir) }
    fun upload(remote: String, file: File) = execute { storeFile(remote, file) }
    fun download(remote: String, outFile: File) = execute { retrieveFile(remote, outFile) }
    fun download(remote: String, outFile: String = remote) =  File(outFile).also { execute { retrieveFile(remote, it) } }

}