package com.osmp4j.host

import com.osmp4j.host.rabbit.DuplicatesService
import com.osmp4j.host.rabbit.PreparationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class InterfaceController @Autowired constructor(private val preparationService: PreparationService, private val duplicatesService: DuplicatesService) {

    @GetMapping
    fun send() = preparationService.prepareBoundingBox()

}