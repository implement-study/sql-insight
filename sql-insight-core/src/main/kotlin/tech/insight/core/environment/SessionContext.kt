package tech.insight.core.environment


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
open class SessionContext(thread: Thread) : AbstractMapContext(), AutoCloseable {

    val id = thread.id


    override fun close() {
        sessionManager.remove(id)
    }

    companion object {

        private val sessionManager = mutableMapOf<Long, SessionContext>()

        fun getSessionContext(): SessionContext {
            return sessionManager.computeIfAbsent(Thread.currentThread().id) { create() }
        }

        fun create(): SessionContext {
            val context = SessionContext(Thread.currentThread())
            sessionManager[context.id] = context
            return context
        }

    }
}
