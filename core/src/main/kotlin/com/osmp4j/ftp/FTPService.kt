package com.osmp4j.ftp

import org.apache.commons.net.ftp.FTPClient
import org.springframework.stereotype.Service
import java.io.File


class FTPClientWrapper(private val ftpClient: FTPClient) {
    fun upload(remote: String, file: File) = ftpClient.storeFile(remote, file.inputStream())
    fun upload(file: File) = upload(file.name, file)
    fun download(remote: String, file: File) = file.also { ftpClient.retrieveFile(remote, it.outputStream()) }
    fun download(remote: String, outFile: String = remote) = download(remote, File(outFile))
    fun downloadAndDelete(remote: String, file: File) = download(remote, file).also { ftpClient.deleteFile(remote) }
    fun downloadAndDelete(remote: String, outFile: String = remote) = downloadAndDelete(remote, File(outFile))
    fun makeDirectory(directory: String) = ftpClient.makeDirectory(directory)
}

@Service
class FTPService(private val ftpClientFactory: FTPClientFactory) {

    fun <T> execute(block: FTPClientWrapper.() -> T): T {
        val client = ftpClientFactory.getClient()
        val result = FTPClientWrapper(client).block()
        client.disconnect()
        return result
    }

    fun mkDir(dir: String) = execute { makeDirectory(dir) }
    fun upload(remote: String, file: File) = execute { upload(remote, file) }

    fun downloadAndDelete(remote: String, outFile: String = remote) = downloadAndDelete(remote, File(outFile))
    fun downloadAndDelete(remote: String, outFile: File) = execute { downloadAndDelete(remote, outFile) }

    fun download(remote: String, outFile: File) = execute { download(remote, outFile) }
    fun download(remote: String, outFile: String = remote) = download(remote, File(outFile))

}