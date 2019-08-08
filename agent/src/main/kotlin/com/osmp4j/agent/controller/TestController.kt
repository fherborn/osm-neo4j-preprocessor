package com.osmp4j.agent.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class TestController @Autowired constructor() {

    private val logger = LoggerFactory.getLogger(TestController::class.java)


}