package com.florianherborn.osmp4j.host

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class TestController @Autowired constructor(private val runner: Runner, private val receiver: Receiver) {

    @GetMapping
    fun send() = runner.run()

}