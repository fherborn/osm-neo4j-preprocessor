package com.osmp4j.host.events

import com.osmp4j.messages.ResultFeatureHolder
import org.springframework.context.ApplicationEvent

class ResultEvent(source: Any, val resultFeatureHolder: ResultFeatureHolder) : ApplicationEvent(source)