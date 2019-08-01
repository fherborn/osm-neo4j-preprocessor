package com.florianherborn.osmp4j.host

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

import java.util.concurrent.CountDownLatch

@Service
class Receiver {

    val latch = CountDownLatch(1)

    fun receiveMessage(message: String) {
        println("Received <$message>")
        latch.countDown()
    }

}