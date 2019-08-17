package com.osmp4j.host.controller.templates

import com.osmp4j.features.core.FeatureType
import com.osmp4j.host.controller.holder.InputHolder
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.springframework.ui.Model

class InputTemplate {

    private fun getInputTemplateHTML(inputForm: InputHolder): String {
        return createHTML().div {
            div("row h-100 justify-content-center align-items-center mt-4") {
                div("card col-lg-4") {
                    h1("card-header info-color white-text text-center display-4") {
                        strong { +"Export" }
                    }
                    div("card-body px-lg-5 pt-0") {
                        inputForm(inputForm)
                    }
                }
            }
        }
    }

    private fun DIV.inputForm(inputForm: InputHolder) {
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

            FeatureType.values().forEach {
                br { }
                checkBoxInput(name = "features", classes = "form-check-input") { id = "$it"; value = "$it" }
                label(classes = "form-check-label") { for_ = "$it"; +it.name }
            }

            postButton(classes = "btn btn-outline-info btn-rounded btn-block z-depth-0 my-4 waves-effect") { +"Senden" }
        }
    }

    fun build(model: Model): String {
        model.addAttribute(TEXT_ATTRIBUTE, getInputTemplateHTML(InputHolder()))
        return INDEX_TEMPLATE
    }

    companion object {
        private const val TEXT_ATTRIBUTE = "text"
        private const val INDEX_TEMPLATE = "index"
    }

}