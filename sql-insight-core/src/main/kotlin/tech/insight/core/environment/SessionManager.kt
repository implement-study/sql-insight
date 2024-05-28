package tech.insight.core.environment

import tech.insight.core.bean.Database
import tech.insight.core.event.DropDatabaseEvent
import tech.insight.core.event.EventListener


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
object SessionManager : EventListener<DropDatabaseEvent> {

    private val sessions: MutableMap<Long, Session> = HashMap()

    private fun currentSessionId(): Long {
        return Thread.currentThread().id
    }

    fun currentSession(): Session {
        return getSession(currentSessionId())
    }

    private fun createSession(sessionId: Long): Session {
        return Session(sessionId)
    }

    fun getSession(sessionId: Long): Session {
        return sessions.computeIfAbsent(sessionId) { createSession(sessionId) }
    }

    fun closeSession(userId: Long) {
        sessions.remove(userId)
    }

    override fun onEvent(event: DropDatabaseEvent) {
        sessions.values.filter { it.database?.name == event.database.name }.forEach { it.database = null }
    }
}

class Session(var id: Long) {

    var database: Database? = null


}
