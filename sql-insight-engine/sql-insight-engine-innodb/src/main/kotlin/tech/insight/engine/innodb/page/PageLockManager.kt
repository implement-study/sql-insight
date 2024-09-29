package tech.insight.engine.innodb.page

import java.util.concurrent.locks.ReentrantReadWriteLock


/**
 *
 * manage lock objects for the entire page
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class PageLockManager(val page: InnoDbPage) {

    /**
     * is and ix lock
     */
    private val isx = ReentrantReadWriteLock()

    val IX = isx.writeLock()
    
    val IS = isx.readLock()
    
    
}
