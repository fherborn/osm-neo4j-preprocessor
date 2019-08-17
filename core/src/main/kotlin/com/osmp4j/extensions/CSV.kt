package com.osmp4j.extensions

import com.osmp4j.csv.CSVReader
import com.osmp4j.csv.CSVWriter
import com.osmp4j.features.core.FeatureFactory
import java.io.File

fun List<Any>.toFinalCSVLine() = joinToString(separator = ",", postfix = "\n")
fun String.toCSVTokes() = split(",")

fun <T : Any> List<T>.toFile(converter: FeatureFactory<T>) = CSVWriter(converter).append(this).getFile()
fun <T : Any> Sequence<T>.toFile(converter: FeatureFactory<T>) = CSVWriter(converter).append(this).getFile()

fun <T : Any> File.forEachCSV(converter: FeatureFactory<T>, block: (T) -> Unit) =
        CSVReader(this, converter).forEach(block)

fun <T : Any> List<File>.forEachCSV(converter: FeatureFactory<T>, block: (T) -> Unit) =
        forEach { it.forEachCSV(converter, block) }

fun <K, T : Any> MutableMap<K, CSVWriter<T>>.getWriter(key: K, converter: FeatureFactory<T>, fileNameGenerator: (key: K) -> String) =
        get(key) ?: (CSVWriter(converter, fileNameGenerator(key)).also { set(key, it) })

fun <K, T : Any> MutableMap<K, CSVWriter<T>>.getFiles() = mapValues { (_, v) -> v.getFile() }

fun <K, T : Any> List<File>.groupByCSV(converter: FeatureFactory<T>, fileNameGenerator: (key: K) -> String, groupBy: (T) -> K): Map<K, File> =
        mutableMapOf<K, CSVWriter<T>>().also { fileWriters ->
            forEachCSV(converter) { obj -> fileWriters.getWriter(groupBy(obj), converter, fileNameGenerator).append(obj) }
        }.getFiles()

fun <T : Any> List<File>.mergeCSV(outFile: String, converter: FeatureFactory<T>): File =
        CSVWriter(converter, outFile).also { writer ->
            forEachCSV(converter) { value ->
                writer.append(value)
            }
        }.getFile()

fun <T : Any> File.readCSVObjects(converter: FeatureFactory<T>): List<T> {
    val result = mutableListOf<T>()
    forEachCSV(converter) { result.add(it) }
    return result
}

fun <T : Any> List<T>.toCSVFile(fileName: String, converter: FeatureFactory<T>): File =
        CSVWriter(converter, fileName).append(this).getFile()

fun <T : Any> File.filterMap(outFile: String, converter: FeatureFactory<T>, filter: List<T>.() -> List<T>) =
        readCSVObjects(converter).filter().toCSVFile(outFile, converter)





