package com.osmp4j.features.core

sealed class Neo4jType(val wrap: (value: String) -> String)
object Neo4jInt : Neo4jType({ "toInteger($it)" })
object Neo4jFloat : Neo4jType({ "toFloat($it)" })
object Neo4jString : Neo4jType({ "toString($it)" })
object AsIs : Neo4jType({ it })