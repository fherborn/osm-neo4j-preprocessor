package com.osmp4j.host.controller

import com.osmp4j.host.controller.templates.InputTemplate
import com.osmp4j.host.controller.holder.InputHolder
import com.osmp4j.host.controller.templates.ResultTemplate
import com.osmp4j.host.events.ResultEvent
import com.osmp4j.host.services.ExportService
import com.osmp4j.messages.TaskInfo
import com.osmp4j.data.BoundingBox
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.async.DeferredResult
import java.util.*
import javax.validation.Valid


@Controller
@RequestMapping
class ExportController @Autowired constructor(private val exportService: ExportService) {

    private val pendingResult = hashMapOf<UUID, Pair<DeferredResult<String>, Model>>()

    @EventListener
    fun handleContextStart(event: ResultEvent) {
        val resultHolder = event.resultFeatureHolder
        val (_, _, task) = resultHolder
        val (result, model) = pendingResult[task.id] ?: return
        val template = ResultTemplate(resultHolder).build(model)
        result.setResult(template)
        pendingResult.remove(task.id)
    }

    @GetMapping
    fun export(model: Model): String = InputTemplate().build(model)


    @PostMapping
    fun exportSubmit(@Valid @ModelAttribute input: InputHolder, model: Model): DeferredResult<String> {
        val task = input.toTask()
        return DeferredResult<String>().also {
            pendingResult[task.id] = it to model
            exportService.startExport(task)
        }
    }


    private fun InputHolder.toTask() = TaskInfo(taskName, BoundingBox.createFixed(fromLat, fromLon, toLat, toLon), features)


}