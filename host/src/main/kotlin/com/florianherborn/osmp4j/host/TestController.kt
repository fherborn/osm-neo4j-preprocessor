package com.florianherborn.osmp4j.host

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class TestController @Autowired constructor(private val preparationService: PreparationService, private val duplicatesService: DuplicatesService) {

    @GetMapping
    fun send() = preparationService.prepateBoundingBox()

}