package com.osmp4j.data

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

interface CSVObject<T> {
    fun getTokens(): List<Any>
}

interface CSVObjectFactory<T : CSVObject<T>> {
    fun getHeaders(): List<String>
    fun fromTokens(tokens: List<String>): T
}

fun List<Any>.toCSVLine() = joinToString(separator = ",", postfix = "\n")
fun String.toCSVTokes() = split(",")

class CSVService {


    fun <T : CSVObject<T>> write(path: String, values: Sequence<T>, factory: CSVObjectFactory<T>) {
        val writer = FileWriter(path)

        val header = factory.getHeaders().toCSVLine()
        writer.append(header)

        values.map { it.getTokens() }
                .map { it.toCSVLine() }
                .forEach { writer.append(it) }

        writer.flush()
        writer.close()
    }

    fun <T : CSVObject<T>> read(path: String, factory: CSVObjectFactory<T>) = read(File(path), factory)
    fun <T : CSVObject<T>> read(file: File, factory: CSVObjectFactory<T>): Sequence<T> {
        val reader = file.bufferedReader()

        var line = reader.readLine()?.let {
            val headers = it.toCSVTokes()
            reader.readLine()
        }

        val result = mutableListOf<T>()
        while (line != null) {
            result.add(factory.fromTokens(line.toCSVTokes()))
            line = reader.readLine()
        }

        reader.close()
        return result.asSequence()
    }
}


fun <T : CSVObject<T>> File.forEachCSV(factory: CSVObjectFactory<T>, hasHeader: Boolean = true, block: (T) -> Unit) =
        CSVReader.create(this, factory, hasHeader).forEach(block)

fun <T : CSVObject<T>> List<File>.forEachCSV(factory: CSVObjectFactory<T>, hasHeader: Boolean = true, block: (T) -> Unit) =
        forEach { it.forEachCSV(factory, hasHeader, block) }

fun <K, T : CSVObject<T>> MutableMap<K, CSVWriter<T>>.getWriter(key: K, factory: CSVObjectFactory<T>, fileNameGenerator: (key: K) -> String) =
        get(key) ?: (CSVWriter.create(fileNameGenerator(key), factory).also { set(key, it) })

fun <K, T : CSVObject<T>> MutableMap<K, CSVWriter<T>>.getFiles() = mapValues { (_, v) -> v.getFile() }

fun <K, T : CSVObject<T>> List<File>.groupByCSV(factory: CSVObjectFactory<T>, fileNameGenerator: (key: K) -> String, groupBy: (T) -> K): Map<K, File> =
        mutableMapOf<K, CSVWriter<T>>().also { fileWriters ->
            forEachCSV(factory) { obj -> fileWriters.getWriter(groupBy(obj), factory, fileNameGenerator).append(obj) }
        }.getFiles()

fun <T : CSVObject<T>> List<File>.mergeCSV(outFile: String, factory: CSVObjectFactory<T>, hasHeader: Boolean = true): File =
        CSVWriter.create(outFile, factory, hasHeader).also { writer ->
            forEachCSV(factory) { value ->
                writer.append(value)
            }
        }.getFile()

fun <T : CSVObject<T>> File.readCSVObjects(factory: CSVObjectFactory<T>, hasHeader: Boolean = true): List<T> {
    val result = mutableListOf<T>()
    forEachCSV(factory, hasHeader) { result.add(it) }
    return result
}

fun <T : CSVObject<T>> List<T>.toCSVFile(fileName: String, factory: CSVObjectFactory<T>, hasHeader: Boolean = true): File =
        CSVWriter.create(fileName, factory, hasHeader).append(this).getFile()

fun <T : CSVObject<T>> File.filterMap(outFile: String, factory: CSVObjectFactory<T>, filter: List<T>.() -> List<T>) =
        readCSVObjects(factory).filter().toCSVFile(outFile, factory)


class CSVReader<T : CSVObject<T>> private constructor(private val file: File, private val factory: CSVObjectFactory<T>, private val hasHeader: Boolean) {

    private val reader: BufferedReader by lazy { file.bufferedReader() }
    val header: String? = if (hasHeader) reader.readLine() else null

//    fun write(file: File) {
//        val writer = file.bufferedWriter()
//
//        val header = factory.getHeaders().toCSVLine()
//        writer.append(header)
//
//        values.map { it.getTokens() }
//                .map { it.toCSVLine() }
//                .forEach { writer.append(it) }
//
//        writer.flush()
//        writer.close()
//    }

    private fun List<String>.toObject() = factory.fromTokens(this)
    private fun readLine(): T? = reader.readLine()?.toCSVTokes()?.toObject()

    fun forEach(f: (T) -> Unit) {
        var obj = readLine()
        while (obj != null) {
            f(obj)
            obj = readLine()
        }
        reader.close()
    }

    companion object {
        fun <T : CSVObject<T>> create(path: String, factory: CSVObjectFactory<T>, hasHeader: Boolean = true): CSVReader<T> = create(File(path), factory, hasHeader)
        fun <T : CSVObject<T>> create(file: File, factory: CSVObjectFactory<T>, hasHeader: Boolean = true) = CSVReader(file, factory, hasHeader)
    }

}

class CSVWriter<T : CSVObject<T>> private constructor(private val file: File, private val factory: CSVObjectFactory<T>, private val hasHeader: Boolean) {

    private val writer: BufferedWriter by lazy { file.bufferedWriter() }

    init {
        if (hasHeader) {
            writer.append(factory.getHeaders().toCSVLine())
        }
    }

    fun append(obj: T) = this.also { writer.append(obj.getTokens().toCSVLine()) }
    fun append(objs: List<T>) = this.also { objs.forEach { append(it) } }

    fun close() {
        writer.flush()
        writer.close()
    }

    fun getFile() = file.also { close() }

    companion object {
        fun <T : CSVObject<T>> create(path: String, factory: CSVObjectFactory<T>, hasHeader: Boolean = true): CSVWriter<T> = create(File(path), factory, hasHeader)
        fun <T : CSVObject<T>> create(file: File, factory: CSVObjectFactory<T>, hasHeader: Boolean = true) = CSVWriter(file, factory, hasHeader)
    }

}