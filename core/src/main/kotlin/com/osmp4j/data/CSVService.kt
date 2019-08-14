package com.osmp4j.data

import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter

interface CSVObject<T> {
    fun getTokens(): List<Any>
}

interface CSVObjectFactory<T : CSVObject<T>> {
    fun getHeaders(): List<String>
    fun fromTokens(tokens: List<String>): T
}

class CSVService {

    fun <T : CSVObject<T>> write(path: String, values: Sequence<T>, factory: CSVObjectFactory<T>) {
        val writer = FileWriter(path)

        val header = factory.getHeaders().joinToString(separator = ",", postfix = "\n")
        writer.append(header)

        values.map { it.getTokens() }
                .map { it.joinToString(separator = ",", postfix = "\n") }
                .forEach { writer.append(it) }

        writer.flush()
        writer.close()
    }

    fun <T : CSVObject<T>> read(path: String, factory: CSVObjectFactory<T>) {
        val reader = BufferedReader(FileReader(path))

        var line = reader.readLine()?.let {
            val headers = it.split(",")
            reader.readLine()
        }

        val result = mutableListOf<T>()
        while (line != null) {
            result.add(factory.fromTokens(line.split(",")))
            line = reader.readLine()
        }

        reader.close()
    }
}