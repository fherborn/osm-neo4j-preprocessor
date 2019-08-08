package com.osmp4j.ftp

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("osmp4j.ftpd")
class FtpProperties {

    @Value("\${osmp4j.ftpd.host}")
    var host: String = ""

    @Value("\${osmp4j.ftpd.user.name}")
    var user: String = ""

    @Value("\${osmp4j.ftpd.user.pass}")
    var pass: String = ""

    var port = 21

}