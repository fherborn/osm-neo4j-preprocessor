package com.osmp4j.data

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.util.*

interface CSVObject<T> {
    fun getTokens(): List<Any>
}

interface CSVObjectFactory<T : CSVObject<T>> {
    fun getHeaders(): List<String>
    fun fromTokens(tokens: List<String>): T
}

fun List<Any>.toFinalCSVLine() = joinToString(separator = ",", postfix = "\n")
fun String.toCSVTokes() = split(",")

fun <T : Any> List<T>.toFile(converter: CSVConverter<T>) = CSVWriterNeu(converter).append(this).getFile()
fun <T : Any> Sequence<T>.toFile(converter: CSVConverter<T>) = CSVWriterNeu(converter).append(this).getFile()

fun <T : Any> File.forEachCSV(converter: CSVConverter<T>, block: (T) -> Unit) =
        CSVReaderNeu(this, converter).forEach(block)

fun <T : Any> List<File>.forEachCSV(converter: CSVConverter<T>, block: (T) -> Unit) =
        forEach { it.forEachCSV(converter, block) }

fun <K, T : Any> MutableMap<K, CSVWriterNeu<T>>.getWriter(key: K, converter: CSVConverter<T>, fileNameGenerator: (key: K) -> String) =
        get(key) ?: (CSVWriterNeu(converter, fileNameGenerator(key)).also { set(key, it) })

fun <K, T : Any> MutableMap<K, CSVWriterNeu<T>>.getFiles() = mapValues { (_, v) -> v.getFile() }

fun <K, T : Any> List<File>.groupByCSV(converter: CSVConverter<T>, fileNameGenerator: (key: K) -> String, groupBy: (T) -> K): Map<K, File> =
        mutableMapOf<K, CSVWriterNeu<T>>().also { fileWriters ->
            forEachCSV(converter) { obj -> fileWriters.getWriter(groupBy(obj), converter, fileNameGenerator).append(obj) }
        }.getFiles()

fun <T : Any> List<File>.mergeCSV(outFile: String, converter: CSVConverter<T>): File =
        CSVWriterNeu(converter, outFile).also { writer ->
            forEachCSV(converter) { value ->
                writer.append(value)
            }
        }.getFile()

fun <T : Any> File.readCSVObjects(converter: CSVConverter<T>): List<T> {
    val result = mutableListOf<T>()
    forEachCSV(converter) { result.add(it) }
    return result
}

fun <T : Any> List<T>.toCSVFile(fileName: String, converter: CSVConverter<T>): File =
        CSVWriterNeu(converter, fileName).append(this).getFile()

fun <T : Any> File.filterMap(outFile: String, converter: CSVConverter<T>, filter: List<T>.() -> List<T>) =
        readCSVObjects(converter).filter().toCSVFile(outFile, converter)


class CSVWriterNeu<T : Any>(private val converter: CSVConverter<T>, fileName: String = "${converter.typeName()}-${UUID.randomUUID()}.csv") {

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


class CSVReaderNeu<T>(private val file: File, private val converter: CSVConverter<T>) {

    private val reader: BufferedReader by lazy { file.bufferedReader().also { it.readLine() } }
    private fun readLine(): T? = reader.readLine()?.let { converter.fromCSV(it) }
    fun forEach(f: (T) -> Unit) {
        var obj = readLine()
        while (obj != null) {
            f(obj)
            obj = readLine()
        }
        reader.close()
    }

}
