package com.osmp4j.host.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such File")
class FileNotFoundException(fileName: String) : RuntimeException("No such File $fileName")
