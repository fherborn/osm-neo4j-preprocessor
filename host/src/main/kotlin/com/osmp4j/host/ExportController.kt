package com.osmp4j.host

import com.osmp4j.core.rabbitmq.models.BoundingBox
import com.osmp4j.host.rabbit.PreparationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid
import kotlin.math.*


@Service
class ExportService(private val preparationService: PreparationService) {

    fun export(taskName: String, fromLat: Double, toLat: Double, fromLon: Double, toLon: Double) {
        val boundingBox = BoundingBox.create(fromLat, toLat, fromLon, toLon)

        val boundingBoxes = getSplittedBoxes(boundingBox)

    }

    private fun getSplittedBoxes(boundingBox: BoundingBox): List<BoundingBox> {
        val (fromLat, toLat, fromLon, toLon) = boundingBox

        TODO("create splittedBoxes")

    }

    private fun distance(fromLat: Double, toLat: Double, fromLon: Double = 1.0, toLon: Double = 1.0): Double {
        val earthRadius = 6378.137
        val distanceLat = calcDistance(toLat, fromLat)
        val distanceLon = calcDistance(toLon, fromLon)
        val a = sin(distanceLat/2) * sin(distanceLat/2) + cos(fromLat * PI /180) * cos(toLat * PI /180) * sin(distanceLon/2) * sin(distanceLon/2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        val d = earthRadius * c
        return d * 1000
    }


    private fun calcDistance(from: Double, to: Double) = toMetric(from) - toMetric(to)

    private fun toMetric(toLat: Double) = toLat * PI / 180


}

@Controller
@RequestMapping
class ExportController @Autowired constructor(private val exportService: ExportService) {

    @GetMapping
    fun export(model: Model): String {
        model.addAttribute(INPUT_ATTRIBUTE, ExportTaskInputForm())
        return INDEX_TEMPLATE
    }

    @PostMapping
    fun exportSubmit(@Valid @ModelAttribute(INPUT_ATTRIBUTE) input: ExportTaskInputForm, model: Model): String {
        model.addAttribute(BOUNDING_BOX_ATTRIBUTE, input)
        exportService.export(input.taskName, input.fromLat, input.toLat, input.fromLon, input.toLon)
        return RESULT_TEMPLATE
    }

    companion object {
        const val INPUT_ATTRIBUTE = "input"
        const val BOUNDING_BOX_ATTRIBUTE = "boundingBox"

        const val INDEX_TEMPLATE = "index"
        const val RESULT_TEMPLATE = "result"
    }

}