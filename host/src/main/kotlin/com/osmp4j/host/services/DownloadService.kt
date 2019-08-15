package com.osmp4j.host.services

import com.osmp4j.ftp.FTPService
import com.osmp4j.messages.ResultFileHolder
import com.osmp4j.messages.TaskInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class DownloadService @Autowired constructor(private val ftpService: FTPService) {

    fun saveForDownload(task: TaskInfo, files: ResultFileHolder) {
        //TODO Create name z.B. <taskName>-<nodes|ways>-<taskId>.csv
        //TODO Move result files with final name to this folder
        //TODO Expose download link to this files
    }

    fun getFile(fileName: String): File {
        //TODO Load files with same name from ftp
        //TODO 404 if not found
//        val path = results[fileName] ?: throw FileNotFoundException(fileName)
//        val file = File(fileName)
//        ftpService.download(path, file)
//        return file
        return TODO()
    }
}