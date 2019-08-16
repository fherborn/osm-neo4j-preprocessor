package com.osmp4j.host.controller

import com.osmp4j.data.CSVConverter
import com.osmp4j.data.Node
import com.osmp4j.data.NodeType
import com.osmp4j.data.WayConverter
import com.osmp4j.host.services.ExportService
import com.osmp4j.messages.TaskInfo
import com.osmp4j.models.BoundingBox
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEvent
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

class PublishResultEvent(source: Any, val nodeFileNames: List<Pair<CSVConverter<Node>, String>>, val waysFileName: String, val task: TaskInfo) : ApplicationEvent(source)


data class InputForm(
        var taskName: String = "OSMExport",
        var fromLat: Double = 7.0862,
        var fromLon: Double = 51.0138,
        var toLat: Double = 7.7344,
        var toLon: Double = 51.3134,
        var features: List<NodeType> = listOf()
)


@Controller
@RequestMapping
class ExportController @Autowired constructor(private val exportService: ExportService) {

    private val pendingResult = hashMapOf<UUID, Pair<DeferredResult<String>, Model>>()
    private val logger = LoggerFactory.getLogger(ExportController::class.java)

    @EventListener
    fun handleContextStart(event: PublishResultEvent) {
        logger.debug("Received publish event")

        val result = pendingResult[event.task.id] ?: return

        result.first.setResult(getResultTemplate(result.second, event))
        logger.debug("Finishing publish event")
        pendingResult.remove(event.task.id)
    }

    @GetMapping
    fun export(model: Model): String = getInputTemplate(model)

    private fun getInputTemplateHTML(inputForm: InputForm): String {
        return createHTML().div {
            div("row h-100 justify-content-center align-items-center mt-4") {
                div("card col-lg-4") {
                    h1("card-header info-color white-text text-center display-4") {
                        strong { +"Export" }
                    }
                    div("card-body px-lg-5 pt-0") {
                        postForm(action = "#", classes = "md-form mt-4") {
                            val formClasses = "form-control"
                            val labelClasses = "mt-4"

                            label(classes = labelClasses) { for_ = "taskName"; +"Task Name" }
                            textInput(name = "taskName", classes = formClasses) { id = "taskName"; value = inputForm.taskName }

                            label(classes = labelClasses) { for_ = "fromLat"; +"From Latitude" }
                            numberInput(name = "fromLat", classes = formClasses) { id = "fromLat"; min = "-85"; max = "85"; step = "any"; value = "${inputForm.fromLat}" }

                            label(classes = labelClasses) { for_ = "fromLon"; +"From Longitude" }
                            numberInput(name = "fromLon", classes = formClasses) { id = "fromLon"; min = "-180"; max = "180"; step = "any"; value = "${inputForm.fromLon}" }

                            label(classes = labelClasses) { for_ = "toLat"; +"To Latitude" }
                            numberInput(name = "toLat", classes = formClasses) { id = "toLat"; min = "-85"; max = "85"; step = "any"; value = "${inputForm.toLat}" }

                            label(classes = labelClasses) { for_ = "toLon"; +"To Longitude" }
                            numberInput(name = "toLon", classes = formClasses) { id = "toLon"; min = "-180"; max = "180"; step = "any"; value = "${inputForm.toLon}" }

                            strong { +"Features" }

                            NodeType.values().forEach {
                                br { }
                                checkBoxInput(name = "features", classes = "form-check-input") { id = "$it"; value = "$it" }
                                label(classes = "form-check-label") { for_ = "$it"; +it.name }
                            }

                            postButton(classes = "btn btn-outline-info btn-rounded btn-block z-depth-0 my-4 waves-effect") { +"Senden" }
                        }
                    }
                }
            }
        }
    }

    private fun getResultTemplateHTML(event: PublishResultEvent): String {

        val task = event.task

        val exampleQueries = listOf(
                "Match 300 connected nodes" to listOf("MATCH (n)-[*]->(connected) RETURN connected LIMIT 300"),
                "Count all nodes" to listOf("MATCH (n:Node) RETURN count(*)"),
                "Remove all connected nodes and ways" to listOf("Match (start)-[way]-(end) DELETE start, way, end"),
                "Remove all unconnected nodes" to listOf("MATCH (n) DELETE n")
        )

        return createHTML().div {
            div("row h-100 justify-content-center align-items-center mt-4") {
                div("card col-lg-8") {
                    h1("card-header info-color white-text text-center display-4") {
                        strong { +"Task ${task.name} finished" }
                    }
                    div("card-body px-lg-5 pt-0") {
                        div(classes = "mt-4") {

                            div(classes = "text-info") {
                                strong { +"Success! " }
                                +("Export finished. To insert the data into Neo4j, first import ALL nodes with the queries below. " +
                                        "Only then the Ways can be added, otherwise the start and end nodes are missing.")
                            }

                            h6(classes = "mt-4") { +"Nodes:" }
                            event.nodeFileNames.forEach { (_, file) ->
                                div(classes = "mt-2") {
                                    br {}
                                    a(href = file) { +file }
                                }
                            }

                            event.nodeFileNames.forEach { (converter, file) ->
                                div(classes = "mt-2") {
                                    em(classes = "text-muted") { +"Import ${converter.typeName()}s: " }
                                    div(classes = "col-12") {
                                        style = codeBoxStyle
                                        converter.getQueryLines(file).forEach {
                                            code { style = codeStyle; +it; }; br {}
                                        }
                                    }
                                }
                            }

                            div(classes = "text-info mt-4") {
                                strong { +"You need an Indexes! " }
                                +("To improve the performance of the import, as well as later queries, an index must be added for the nodes.")
                            }

                            div(classes = "mt-2") {
                                em(classes = "text-muted") { +"Set node index: " }
                                div(classes = "col-12") {
                                    style = codeBoxStyle
                                    code { style = codeStyle; +"CREATE INDEX ON :Node(id);" }; br {}
                                }
                            }

                            h6(classes = "mt-4") { +"Ways:" }
                            br {}
                            a(href = event.waysFileName, classes = "mt-2") { +event.waysFileName }

                            em(classes = "text-muted") { +"Import ways: " }
                            div(classes = "mt-2") {
                                div(classes = "col-12") {
                                    style = codeBoxStyle
                                    WayConverter.getQueryLines(event.waysFileName).forEach {
                                        code { style = codeStyle; +it; }; br {}
                                    }
                                }
                            }

                            div(classes = "text-info mt-4") {
                                strong { +"Same here! " }
                                +("Also for the ways indexes are needed.")
                            }

                            div(classes = "mt-2") {
                                em(classes = "text-muted") { +"Set node index: " }
                                div(classes = "col-12") {
                                    style = codeBoxStyle
                                    code { style = codeStyle; +"CREATE INDEX ON :Way(id);" }; br {}
                                }
                            }

                            div(classes = "text-info mt-4") {
                                strong { +"Finished! " }
                                +("After completing the above steps, the data is successfully imported. To help you get started with the data, follow a few sample queries.")
                            }

                            exampleQueries.forEach { (name, lines) ->
                                div(classes = "mt-2") {
                                    em(classes = "text-muted") { +("$name:") }
                                    div(classes = "col-12") {
                                        style = codeBoxStyle
                                        lines.forEach {
                                            code { style = codeStyle; +it; }; br {}
                                        }
                                    }
                                }
                            }

                            getButton(classes = "btn btn-outline-info btn-rounded btn-block z-depth-0 my-4 waves-effect") { +"Create next Task" }
                        }
                    }
                }
            }
        }
    }


    private val codeStyle = "color: rgb(244, 244, 244); " +
            "font-family: \"Inconsolata\", \"Monaco\", \"Lucida Console\", Courier, monospace; " +
            "font-size: 14px; " +
            "line-height: 30.3167px; "

    private val codeBoxStyle = "padding: 1.5rem; " +
            "background-color: rgb(51, 51, 51); " +
            "border: rgb(182, 173, 173) 1px dotted; " +
            "border-radius: 2px; " +
            codeStyle


    @PostMapping
    fun exportSubmit(@Valid @ModelAttribute input: InputForm, model: Model): DeferredResult<String> {
        val task = input.toTask()

        logger.debug("$task")

        val deferredResult = DeferredResult<String>()
        pendingResult[task.id] = deferredResult to model

        exportService.startExport(task)
        return deferredResult
    }


    private fun InputForm.toTask() = TaskInfo(taskName, BoundingBox.createFixed(fromLat, fromLon, toLat, toLon), features)

    private fun getResultTemplate(model: Model, event: PublishResultEvent): String {
        model.addAttribute(TEXT_ATTRIBUTE, getResultTemplateHTML(event))
        return RESULT_TEMPLATE
    }

    private fun getInputTemplate(model: Model): String {
        model.addAttribute(TEXT_ATTRIBUTE, getInputTemplateHTML(InputForm()))
        return INDEX_TEMPLATE
    }


    companion object {
        const val TEXT_ATTRIBUTE = "text"

        const val INDEX_TEMPLATE = "index"
        const val RESULT_TEMPLATE = "result"
    }

}