package com.osmp4j.extensions

import com.osmp4j.models.Identifiable
import java.util.*
import kotlin.NoSuchElementException


fun <T, O> HashMap<T, O>.pop(key: T) = getOrThrow(key).also { remove(key) }
fun <T, O> HashMap<T, O>.getOrThrow(key: T) = get(key) ?: throw NoSuchElementException()

fun <T : Identifiable> HashMap<UUID, T>.put(identity: T) {
    this[identity.id] = identity
}

fun <T : Identifiable> identityMapOf(vararg identities: T) = hashMapOf(*identities.map { it.id to it }.toTypedArray())