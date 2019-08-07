package com.osmp4j.host

import com.osmp4j.core.rabbitmq.models.BoundingBox
import com.osmp4j.host.rabbit.PreparationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
class ExportController @Autowired constructor(private val preparationService: PreparationService) {

    private val logger = LoggerFactory.getLogger(ExportController::class.java)

    @GetMapping
    fun export(model: Model) = getInputTemplate(model)

    @PostMapping
    fun exportSubmit(@Valid @ModelAttribute(INPUT_ATTRIBUTE) input: ExportTaskInputForm, model: Model): String {
        logger.receivedInput(input)
        preparationService.prepare(input.taskName, BoundingBox.from(input))
        return getResultTemplate(model, input)
    }

    private fun Logger.receivedInput(input: ExportTaskInputForm) =
            this.debug("Received task: ${input.taskName} with bounding box: ${input.fromLat}, ${input.fromLon}, ${input.toLat}, ${input.toLon}")

    private fun BoundingBox.Companion.from(input: ExportTaskInputForm) =
            BoundingBox.createFixed(input.fromLat, input.fromLon, input.toLat, input.toLon)


    private fun getResultTemplate(model: Model, input: ExportTaskInputForm): String {
        model.addAttribute(BOUNDING_BOX_ATTRIBUTE, input)
        return RESULT_TEMPLATE
    }

    private fun getInputTemplate(model: Model): String {
        model.addAttribute(INPUT_ATTRIBUTE, ExportTaskInputForm()).let { INDEX_TEMPLATE }
        return INDEX_TEMPLATE
    }

    companion object {
        const val INPUT_ATTRIBUTE = "input"
        const val BOUNDING_BOX_ATTRIBUTE = "boundingBox"

        const val INDEX_TEMPLATE = "index"
        const val RESULT_TEMPLATE = "result"
    }

}