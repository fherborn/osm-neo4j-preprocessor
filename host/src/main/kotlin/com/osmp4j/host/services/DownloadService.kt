package com.osmp4j.host.services

import com.osmp4j.data.CSVConverter
import com.osmp4j.data.Node
import com.osmp4j.data.getConverter
import com.osmp4j.ftp.FTPService
import com.osmp4j.host.controller.PublishResultEvent
import com.osmp4j.host.exceptions.FileNotFoundException
import com.osmp4j.messages.ResultFileHolder
import com.osmp4j.messages.TaskInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException


@Service
class DownloadService @Autowired constructor(private val ftpService: FTPService, private val applicationEventPublisher: ApplicationEventPublisher) {

    @Value("\${host}")
    private lateinit var host: String

    private val logger = LoggerFactory.getLogger(DownloadService::class.java)

    fun saveForDownload(task: TaskInfo, files: ResultFileHolder) {
        fun <T> nodeFileName(converter: CSVConverter<T>) = "${task.name.trim()}-${converter.typeName()}-${task.id}.csv"
        val waysFileName = "${task.name.trim()}-ways-${task.id}.csv"

        val nodeFileNames = mutableListOf<Pair<CSVConverter<Node>, String>>()


        ftpService.execute {
            makeDirectory(DOWNLOAD_FOLDER)
            files.nodesFile
                    .map { getConverter(it.first) to it.second }
                    .forEach { (converter, file) ->
                        val fileName = nodeFileName(converter)
                        nodeFileNames.add(converter to fileName)
                        upload("$DOWNLOAD_FOLDER/$fileName", file)
                    }
            upload("$DOWNLOAD_FOLDER/$waysFileName", files.waysFile)
        }

        files.nodesFile.forEach { it.second.delete() }
        files.waysFile.delete()

        val host = if (host.startsWith("http")) host else "http://$host"

        val nodesFiles = nodeFileNames.map { it.first to "$host/downloads/exports/${it.second}" }
        val waysFile = "$host/downloads/exports/$waysFileName"

        logger.debug("Sending publish event")
        val event = PublishResultEvent(this, nodesFiles, waysFile, task)
        applicationEventPublisher.publishEvent(event)

    }

    fun getFile(fileName: String): File {
        val file = File(fileName)
        try {
            ftpService.download("$DOWNLOAD_FOLDER/$fileName", file)
        } catch (e: IOException) {
            throw FileNotFoundException(fileName)
        }
        return file
    }


    companion object {
        const val DOWNLOAD_FOLDER = "downloads"
    }
}