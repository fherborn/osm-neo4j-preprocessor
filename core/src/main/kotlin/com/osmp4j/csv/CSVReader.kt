package com.osmp4j.csv

import com.osmp4j.features.core.FeatureFactory
import java.io.BufferedReader
import java.io.File

class CSVReader<T>(private val file: File, private val converter: FeatureFactory<T>) {

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