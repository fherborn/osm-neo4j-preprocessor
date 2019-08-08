package com.osmp4j.host

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.osmp4j")
class HostApplication

fun main(args: Array<String>) {
    runApplication<HostApplication>(*args)
}
