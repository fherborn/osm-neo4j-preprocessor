package com.osmp4j.host.controller

import com.osmp4j.host.services.ExportService
import com.osmp4j.messages.TaskInfo
import com.osmp4j.models.BoundingBox
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

data class InputForm(
        var taskName: String = "OSMExport",
        var email: String = "",
        var fromLat: Double = 7.0862,
        var fromLon: Double = 51.0138,
        var toLat: Double = 7.7344,
        var toLon: Double = 51.3134
)


@Controller
@RequestMapping
class ExportController @Autowired constructor(private val exportService: ExportService) {

    @GetMapping
    fun export(model: Model) = getInputTemplate(model)

    @PostMapping
    fun exportSubmit(@Valid @ModelAttribute(INPUT_ATTRIBUTE) input: InputForm, model: Model): String {
        exportService.startExport(input.toTask())
        return getResultTemplate(model, input)
    }

    private fun InputForm.toTask() = TaskInfo(taskName, email, BoundingBox.createFixed(fromLat, fromLon, toLat, toLon))

    private fun getResultTemplate(model: Model, input: InputForm): String {
        model.addAttribute(RESULT_ATTRIBUTE, input)
        return RESULT_TEMPLATE
    }

    private fun getInputTemplate(model: Model): String {
        model.addAttribute(INPUT_ATTRIBUTE, InputForm())
        return INDEX_TEMPLATE
    }

    companion object {
        const val INPUT_ATTRIBUTE = "input"
        const val RESULT_ATTRIBUTE = "result"

        const val INDEX_TEMPLATE = "index"
        const val RESULT_TEMPLATE = "result"
    }

}