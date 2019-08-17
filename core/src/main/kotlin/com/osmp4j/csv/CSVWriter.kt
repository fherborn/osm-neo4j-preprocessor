package com.osmp4j.csv

import com.osmp4j.features.core.FeatureFactory
import java.io.BufferedWriter
import java.io.File
import java.util.*

class CSVWriter<T : Any>(private val converter: FeatureFactory<T>, fileName: String = "${converter.typeName()}-${UUID.randomUUID()}.csv") {

    private val file = File(fileName)
    private val writer: BufferedWriter by lazy { file.bufferedWriter() }

    init {
        writer.append(converter.getFinalHeader())
    }

    fun append(obj: T) = this.also { writer.append(converter.toFinalCSVLine(obj)) }
    fun append(objs: List<T>) = this.also { objs.forEach { append(it) } }
    fun append(objs: Sequence<T>) = this.also { objs.forEach { append(it) } }

    fun close() {
        writer.flush()
        writer.close()
    }

    fun getFile() = file.also { close() }
}
