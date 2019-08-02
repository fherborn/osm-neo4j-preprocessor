package com.osmp4j.host

import com.osmp4j.host.rabbit.DuplicatesService
import com.osmp4j.host.rabbit.PreparationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import javax.validation.Valid


data class ExportForm(

        var taskName: String = "Task-${UUID.randomUUID()}",

        var fromLat: Double = 7.0862,

        var fromLon: Double = 51.0138,

        var toLat: Double = 7.7344,

        var toLon: Double = 51.3134

)

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
//        model.addAttribute("export", ExportForm())
//        return "export"
//    }

    @GetMapping
    fun export(model: Model): String {
        model.addAttribute("exportForm", ExportForm())
        return "index"
    }

    @PostMapping
    fun greetingSubmit(@Valid @ModelAttribute("exportForm") exportForm: ExportForm, model: Model): String {
        return "result"
    }

}