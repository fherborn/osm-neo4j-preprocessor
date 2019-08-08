package com.osmp4j.agent.controller

import com.osmp4j.agent.toQuery
import com.osmp4j.http.HttpService
import com.osmp4j.mq.BoundingBox
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestController @Autowired constructor() {

    private val logger = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping("osm")
    fun testOsm(httpService: HttpService) {
        val boundingBox = BoundingBox.createFixed(7.84754, 51.02836, 7.84938, 51.0298)
        val response = httpService.download(boundingBox.toQuery("node"))
        logger.debug("Received from osm api: $response")
    }

}