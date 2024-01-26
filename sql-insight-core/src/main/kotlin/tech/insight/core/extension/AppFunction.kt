package tech.insight.core.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory


inline fun <reified C> slf4j(): Logger = LoggerFactory.getLogger(C::class.java)
