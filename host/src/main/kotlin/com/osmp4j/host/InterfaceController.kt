package com.osmp4j.host

import com.osmp4j.host.models.BoundingBox
import com.osmp4j.host.rabbit.DuplicatesService
import com.osmp4j.host.rabbit.PreparationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid


@Controller
@RequestMapping
class InterfaceController @Autowired constructor(private val preparationService: PreparationService, private val duplicatesService: DuplicatesService) {

//    @GetMapping
//    fun send() = preparationService.prepareBoundingBox()

/*
    Example to fill html model
    @GetMapping("/export")
    fun export(@RequestParam(name = "name", required = false, defaultValue = "World") name: String, model: Model): String {
        model.addAttribute("name", name)
        return "index"
    }
*/


//    @GetMapping("/export")
//    fun greetingForm(model: Model): String {
//        model.addAttribute("export", BoundingBox())
//        return "export"
//    }

    @GetMapping
    fun export(model: Model): String {
        model.addAttribute("boundingBox", BoundingBox())
        return "index"
    }

    @PostMapping
    fun greetingSubmit(@Valid @ModelAttribute("boundingBox") boundingBox: BoundingBox, model: Model): String {
        return "result"
    }

}