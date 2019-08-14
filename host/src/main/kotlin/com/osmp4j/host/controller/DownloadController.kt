package com.osmp4j.host.controller

import com.osmp4j.host.services.ExportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("downloads")
class DownloadController @Autowired constructor(private val exportService: ExportService) {

    @GetMapping("/exports/{name}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getFile(@PathVariable name: String, response: HttpServletResponse): FileSystemResource {
        val file = exportService.getFile(name)
        return FileSystemResource(file)
    }

}