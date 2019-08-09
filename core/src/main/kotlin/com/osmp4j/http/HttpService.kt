package com.osmp4j.http

import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File

sealed class DownloadResult
data class DownloadedFile(val file: File) : DownloadResult()
sealed class DownloadError(val status: Int, val reason: String) : DownloadResult()
class BadRequestError(reason: String) : DownloadError(HttpStatus.SC_BAD_REQUEST, reason)
class BandwidthExceededError(reason: String, val timeout: Int) : DownloadError(509, reason)

@Service
class HttpService {

    private val logger = LoggerFactory.getLogger(HttpService::class.java)

    fun download(url: String, outFileName: String): DownloadResult {

        logger.debug("Start downloading from $url to $outFileName.")
        val httpClient = HttpClientBuilder.create().build()

        val request = HttpGet(url)
        val response = httpClient.execute(request)
        logger.debug("Status: ${response.statusLine.statusCode} ${response.statusLine.reasonPhrase}")

        val code = response.statusLine.statusCode
        if (code != HttpStatus.SC_OK) {
            when (code) {
                400 -> return BadRequestError(response.statusLine.reasonPhrase)
                509 -> return BandwidthExceededError(response.statusLine.reasonPhrase, 30)
            }
        }

        val bis = BufferedInputStream(response.entity.content)

        val file = File(outFileName).apply { createNewFile() }
        val bos = BufferedOutputStream(file.outputStream())

        while (true) {
            val inByte = bis.read()
            if (inByte == -1) break
            bos.write(inByte)
        }

        bis.close()
        bos.close()

        logger.debug("Finish downloading.")

        return DownloadedFile(file)

    }
}