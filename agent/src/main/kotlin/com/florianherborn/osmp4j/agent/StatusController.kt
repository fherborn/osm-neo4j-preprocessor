package com.florianherborn.osmp4j.agent

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class StatusController {
    @GetMapping
    fun status() = "running"
}