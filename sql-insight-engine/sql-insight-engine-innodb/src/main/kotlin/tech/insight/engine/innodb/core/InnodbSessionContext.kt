package tech.insight.engine.innodb.core

import java.util.concurrent.ConcurrentHashMap
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

    private val modifyPages = HashMap<Int, InnoDbPage>()

    fun modifyPage(page: InnoDbPage) {
        modifyPages[page.fileHeader.offset] = page
    }

    override fun close() {
        modifyPages.values.forEach { PageSupport.flushPage(it) }
        sessionManager.remove(id)
    }

    companion object {

        private val sessionManager = ConcurrentHashMap<Long, InnodbSessionContext>()

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
