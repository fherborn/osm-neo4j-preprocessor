package com.osmp4j.http

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class HttpService {

    private val logger = LoggerFactory.getLogger(HttpService::class.java)

    fun download(queryBody: String): String {

        logger.debug("Downloading")

        val url = "http://overpass-api.de/api/interpreter"

        val entity = StringEntity(queryBody,
                ContentType.APPLICATION_FORM_URLENCODED)

        val httpClient = HttpClientBuilder.create().build()
        val request = HttpPost(url)
        request.entity = entity

        val response = httpClient.execute(request)

        return EntityUtils.toString(response.entity, "UTF-8")

    }
}