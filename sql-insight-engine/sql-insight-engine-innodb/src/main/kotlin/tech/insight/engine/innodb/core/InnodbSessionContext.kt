package tech.insight.engine.innodb.core

import tech.insight.core.environment.SessionContext
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.utils.PageSupport


/**
 *
 * innodb session context
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InnodbSessionContext(thread: Thread = Thread.currentThread()) : SessionContext(thread) {

    private val modifyPages = mutableSetOf<InnoDbPage>()

    fun modifyPage(page: InnoDbPage) {
        modifyPages.add(page)
    }

    override fun close() {
        modifyPages.forEach { PageSupport.flushPage(it) }
        sessionManager.remove(id)
    }

    companion object {

        private val sessionManager = mutableMapOf<Long, InnodbSessionContext>()

        fun getInnodbSessionContext(): InnodbSessionContext {
            return sessionManager.computeIfAbsent(Thread.currentThread().id) { create() }
        }

        fun create(): InnodbSessionContext {
            val context = InnodbSessionContext(Thread.currentThread())
            sessionManager[context.id] = context
            return context
        }

    }

}
