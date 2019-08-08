package com.osmp4j.agent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.osmp4j")
class AgentApplication

fun main(args: Array<String>) {
	runApplication<AgentApplication>(*args)
}
