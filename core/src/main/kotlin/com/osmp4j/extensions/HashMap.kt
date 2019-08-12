package com.osmp4j.extensions

import com.osmp4j.models.Identifiable
import java.util.*


fun <T, O> HashMap<T, O>.pop(key: T) = get(key)?.also { remove(key) }

fun <T : Identifiable> HashMap<UUID, T>.put(identity: T) {
    this[identity.id] = identity
}

fun <T : Identifiable> identityMapOf(vararg identities: T) = hashMapOf(*identities.map { it.id to it }.toTypedArray())