package com.osmp4j.features.core

import com.osmp4j.data.Node
import com.osmp4j.data.osm.elements.OSMNode

interface BaseNodeFeatureFactory<T : Node> : FeatureFactory<T> {

    override fun getIndex() = "${getLabels()[0]}(${getIndexAttribute()})"
    fun getIndexAttribute(): String

    override fun fromCSV(csvLine: String) = fromCSV(csvLine.splitCSV())
    fun isType(osmNode: OSMNode): Boolean
    fun getLabels(): List<String>
    fun fromOSM(value: OSMNode): T


    private fun getQueryLabels() = getLabels().joinToString(separator = ":")
    private fun getCreateLine(lineName: String) = getHeader().zip(getTypes())
            .joinToString(prefix = "CREATE (${typeName()}:${getQueryLabels()} {", postfix = "})") { "${it.first}: ${it.second.wrap("$lineName.${it.first}")}" }


    override fun getQueryLines(file: String) = listOf("LOAD CSV WITH HEADERS ", "FROM \"$file\" AS line", getCreateLine("line"))
}


interface FeatureFactory<out T> {
    fun typeName(): String
    fun getHeader(): List<String>
    fun getFinalHeader(): String = getHeader().joinToCSV()
    fun toFinalCSVLine(obj: Any): String = toCSV(obj).map { it.toString() }.joinToCSV()
    fun fromCSV(csvLine: String) = fromCSV(csvLine.splitCSV())
    fun toCSV(obj: Any): List<Any>
    fun fromCSV(tokens: List<String>): T
    fun getTypes(): List<Neo4jType>
    fun getQueryLines(file: String): List<String>
    fun getIndex(): String
}

abstract class ExtendNodeFeatureFactory<T : E, E : Node>(private val extend: BaseNodeFeatureFactory<E>) : BaseNodeFeatureFactory<T> {
    override fun getIndexAttribute() = extend.getIndexAttribute()
    override fun getHeader() = extend.getHeader() + getExtensionHeader()
    abstract fun getExtensionHeader(): List<String>

    override fun getTypes() = extend.getTypes() + getExtensionTypes()
    abstract fun getExtensionTypes(): List<Neo4jType>

    override fun toCSV(obj: Any) = extend.toCSV(obj) + getExtensionValues(obj as T)
    abstract fun getExtensionValues(obj: T): List<Any>

    override fun getLabels() = listOf(getExtensionLabel()) + extend.getLabels()
    abstract fun getExtensionLabel(): String
}

fun List<String>.joinToCSV(lineBreak: Boolean = true) = joinToString(separator = ",", postfix = if (lineBreak) "\n" else "")
fun String.splitCSV() = split(",")