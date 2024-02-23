package tech.insight.core.environment

import tech.insight.core.bean.Database
import java.util.concurrent.atomic.AtomicLong


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
object SessionManager {

    private val sessions: MutableMap<Long, Session> = HashMap()

    private val id = AtomicLong(1)

    private fun currentSessionId(): Long {
        return 1L
    }

    fun currentSession(): Session {
        return getSession(currentSessionId())
    }

    private fun createSession(): Session {
        return Session(id.getAndIncrement())
    }

    fun getSession(userId: Long): Session {
        return sessions.computeIfAbsent(userId) { createSession() }
    }

    fun closeSession(userId: Long) {
        sessions.remove(userId)
    }
}

class Session(var id: Long) {
    lateinit var database: Database
}
