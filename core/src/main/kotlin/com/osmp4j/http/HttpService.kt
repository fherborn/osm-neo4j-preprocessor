package com.osmp4j.http

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File


@Service
class HttpService {

    private val logger = LoggerFactory.getLogger(HttpService::class.java)

    fun download(url: String, outFileName: String): File {

        logger.debug("Start downloading from $url to $outFileName.")
        val httpClient = HttpClientBuilder.create().build()

        val request = HttpGet(url)
        val response = httpClient.execute(request)

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

        return file

    }
}