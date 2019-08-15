package com.osmp4j.host.services

import com.osmp4j.ftp.FTPService
import com.osmp4j.host.exceptions.FileNotFoundException
import com.osmp4j.messages.ResultFileHolder
import com.osmp4j.messages.TaskInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.net.InetAddress


@Service
class DownloadService @Autowired constructor(private val ftpService: FTPService) {

    private val logger = LoggerFactory.getLogger(DownloadService::class.java)

    fun saveForDownload(task: TaskInfo, files: ResultFileHolder) {
        val nodeFileName = "${task.name.trim()}-nodes-${task.id}.csv"
        val waysFileName = "${task.name.trim()}-ways-${task.id}.csv"

        ftpService.execute {
            makeDirectory(DOWNLOAD_FOLDER)
            upload("$DOWNLOAD_FOLDER/$nodeFileName", files.nodesFile)
            upload("$DOWNLOAD_FOLDER/$waysFileName", files.waysFile)
        }

        files.nodesFile.delete()
        files.waysFile.delete()

        val hostName = InetAddress.getLocalHost().hostAddress

        val nodesFile = "http://192.168.0.192:8080/downloads/exports/$nodeFileName"
        val waysFile = "http://192.168.0.192:8080/downloads/exports/$waysFileName"

        //TODO Send E-Mail
        //TODO Find correct ip address
        logger.debug("""Files available at:
            nodes -> $nodesFile
            ways -> $waysFile
            
            Import :
            
            LOAD CSV WITH HEADERS 
            FROM "$nodesFile" AS line
            CREATE (n:Node {id: line.id, lat: toFloat(line.lat), lon: toFloat(line.lon)});
            
            CREATE INDEX ON :Node(id);
        
            USING PERIODIC COMMIT 500
            LOAD CSV WITH HEADERS FROM "$waysFile" AS line
            MATCH (start:Node { id: line.start }),(end:Node { id: line.end})
            CREATE (start)-[:Way { id: line.id, osmId: toInt(line.osmId), distance: toFloat(line.distance) }]->(end);
            
            CREATE INDEX ON :Way(id);
            
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