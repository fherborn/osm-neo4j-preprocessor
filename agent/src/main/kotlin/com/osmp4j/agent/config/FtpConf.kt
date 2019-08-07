package com.osmp4j.agent.config

import com.osmp4j.core.rabbitmq.FTPClientFactory
import com.osmp4j.core.rabbitmq.FtpProperties
import com.osmp4j.core.rabbitmq.FtpService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties("osmp4j.ftpd")
class SpringFtpProperties: FtpProperties {
    @Value("\${osmp4j.ftpd.host}")
    override lateinit var host: String

    @Value("\${osmp4j.ftpd.user.name}")
    override lateinit var user: String

    @Value("\${osmp4j.ftpd.user.pass}")
    override lateinit var pass: String

    override var port = 21
}

@Configuration
class FtpConfiguration @Autowired constructor() {
    @Bean
    fun ftpClientFactory(props: SpringFtpProperties) = FTPClientFactory(props)

    @Bean
    fun ftpService(clientFactory: FTPClientFactory) = FtpService(clientFactory)
}

