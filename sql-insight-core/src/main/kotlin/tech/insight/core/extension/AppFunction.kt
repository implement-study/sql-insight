package tech.insight.core.extension

import com.google.common.collect.Table
import org.slf4j.Logger
import org.slf4j.LoggerFactory


inline fun <reified C> slf4j(): Logger = LoggerFactory.getLogger(C::class.java)


typealias GuavaTable<A, B, C> = Table<A, B, C>
