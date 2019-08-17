package com.osmp4j.host.controller.templates


import com.osmp4j.features.WayFeatureFactory
import com.osmp4j.features.core.BaseNodeFeatureFactory
import com.osmp4j.messages.ResultFeatureHolder
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.springframework.ui.Model

class ResultTemplate(private val holder: ResultFeatureHolder) {

    private val task = holder.task

    private val exampleQueries = listOf(
            "Match 300 connected nodes" to listOf("MATCH (n)-[*]->(connected) RETURN connected LIMIT 300"),
            "Count all nodes" to listOf("MATCH (n:Node) RETURN count(*)"),
            "Remove all connected nodes and ways" to listOf("Match (start)-[way]-(end) DELETE start, way, end"),
            "Remove all unconnected nodes" to listOf("MATCH (n) DELETE n")
    )

    private fun getResultTemplateHTML(): String {


        return createHTML().div {
            div("row h-100 justify-content-center align-items-center mt-4") {
                div("card col-lg-8") {
                    h1("card-header info-color white-text text-center display-4") {
                        strong { +"${task.name} finished" }
                    }
                    div("card-body px-lg-5 pt-0") {
                        div(classes = "mt-4") {
                            startInfo()
                            nodesSection()
                            waysSection()
                            exampleSection()
                            backButton()
                        }
                    }
                }
            }
        }
    }

    private fun DIV.exampleSection() {
        exampleInfo()
        exampleQueries(exampleQueries)
    }

    private fun DIV.waysSection() {
        waysHeader()
        waysLink()
        wayQueries()
        wayIndexInfo()
        wayIndexQuery()
    }

    private fun DIV.nodesSection() {
        nodesHeader()
        nodeLinks()
        nodeQueries()
        nodeIndexInfo()
        nodeIndexQuery()
    }

    private fun DIV.backButton() {
        getButton(classes = "btn btn-outline-info btn-rounded btn-block z-depth-0 my-4 waves-effect") { +"Create next Task" }
    }

    private fun DIV.exampleQueries(exampleQueries: List<Pair<String, List<String>>>) {
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
    }

    private fun DIV.exampleInfo() {
        div(classes = "text-info mt-4") {
            strong { +"Finished! " }
            +("After completing the above steps, the data is successfully imported. To help you get started with the data, follow a few sample queries.")
        }
    }

    private fun DIV.wayIndexQuery() {
        div(classes = "mt-2") {
            em(classes = "text-muted") { +"Set way index: " }
            div(classes = "col-12") {
                style = codeBoxStyle
                code { style = codeStyle; +"CREATE INDEX ON :Way(id);" }; br {}
            }
        }
    }

    private fun DIV.wayIndexInfo() {
        div(classes = "text-info mt-4") {
            strong { +"Same here! " }
            +("Also for the ways indexes are needed.")
        }
    }

    private fun DIV.wayQueries() {
        br {}
        em(classes = "text-muted") { +"Import ways: " }
        div(classes = "mt-2") {
            div(classes = "col-12") {
                style = codeBoxStyle
                WayFeatureFactory.getQueryLines(holder.waysFileName).forEach {
                    code { style = codeStyle; +it; }; br {}
                }
            }
        }
    }

    private fun DIV.waysLink() {
        a(href = holder.waysFileName, classes = "mt-2") { +holder.waysFileName }
    }

    private fun DIV.waysHeader() {
        h6(classes = "mt-4") { +"Ways:" }
    }

    private fun DIV.nodesHeader() {
        h6(classes = "mt-4") { +"Nodes:" }
    }

    private fun DIV.nodeIndexQuery() {
        holder.nodeFileNames.forEach { (converter, _) ->
            div(classes = "mt-2") {
                em(classes = "text-muted") { +"Create ${converter.typeName()} index: " }
                div(classes = "col-12") {
                    style = codeBoxStyle
                    code { style = codeStyle; +"CREATE INDEX ON :${converter.getIndex()};" }; br {}
                }
            }
        }

    }

    private fun DIV.nodeIndexInfo() {
        div(classes = "text-info mt-4") {
            strong { +"You need Indexes! " }
            +("To improve the performance of the import, as well as later queries, an index must be added for the nodes.")
        }
    }

    private fun DIV.nodeLinks() {
        holder.nodeFileNames.forEach { (_, file) ->
            div(classes = "mt-2") {
                a(href = file) { +file }
            }
        }
    }

    private fun DIV.startInfo() {
        div(classes = "text-info") {
            strong { +"Success! " }
            +("Export finished. To insert the data into Neo4j, first import ALL nodes with the queries below. " +
                    "Only then the Ways can be added, otherwise the start and end nodes are missing.")
        }
    }

    private fun DIV.nodeQueries() {
        holder.nodeFileNames.forEach { (converter, file) ->
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

    fun build(model: Model): String {
        model.addAttribute(TEXT_ATTRIBUTE, getResultTemplateHTML())
        return RESULT_TEMPLATE
    }

    companion object {
        private const val TEXT_ATTRIBUTE = "text"
        private const val RESULT_TEMPLATE = "result"
    }

}