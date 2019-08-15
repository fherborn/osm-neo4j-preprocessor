package com.osmp4j.host.services

import com.osmp4j.ftp.FTPService
import com.osmp4j.host.exceptions.FileNotFoundException
import com.osmp4j.messages.ResultFileHolder
import com.osmp4j.messages.ResultFileNameHolder
import com.osmp4j.messages.TaskInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.net.InetAddress

data class ResultHolder (val files : List<File>)


@Service
class DownloadService @Autowired constructor(private val ftpService: FTPService) {

    private val logger = LoggerFactory.getLogger(DownloadService::class.java)

    fun saveForDownload(task: TaskInfo, files: ResultFileHolder) {
        val nodeFileName = "${task.name}-nodes-${task.id}.csv"
        val waysFileName = "${task.name}-ways-${task.id}.csv"

        ftpService.upload("$DOWNLOAD_FOLDER/$nodeFileName", files.nodesFile)
        ftpService.upload("$DOWNLOAD_FOLDER/$waysFileName", files.waysFile)

        val hostName = InetAddress.getLocalHost().hostAddress

        //TODO Send E-Mail
        logger.debug("""Files available at:
            nodes -> $hostName:8080/$nodeFileName
            ways -> $hostName:8080/$waysFileName
        """.trimIndent())

    }

    fun getFile(fileName: String): File {
        val file = File(fileName)
        try {
            ftpService.download("$DOWNLOAD_FOLDER/$fileName", file)
        } catch (e : IOException)  {
            throw FileNotFoundException(fileName)
        }
        return file
    }


    companion object {
        const val DOWNLOAD_FOLDER = "downloads"
    }
}