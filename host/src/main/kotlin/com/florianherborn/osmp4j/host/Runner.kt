package com.florianherborn.osmp4j.host

import com.florianherborn.osmp4j.host.config.RabbitConf
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class Runner @Autowired constructor(private val receiver: Receiver, private val rabbitTemplate: RabbitTemplate) : CommandLineRunner {

    override fun run(vararg args: String) {
        println("Sending message...")
        rabbitTemplate.convertAndSend(RabbitConf.TOPIC_EXCHANGE_NAME, "foo.bar.baz", "Hello from RabbitMQ!")
        receiver.latch.await(10000, TimeUnit.MILLISECONDS)
    }

}