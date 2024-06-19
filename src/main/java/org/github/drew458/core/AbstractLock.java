package org.github.drew458.core;

import org.github.drew458.model.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractLock {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    // TODO implement tryLock(long time, TimeUnit unit)

    abstract void lock(Lock lock);

    abstract Boolean tryLock(Lock lock);

    /**
     * Releases a previously-acquired exclusive session-level lock.
     * If the lock was not held by the current session the method will still return normally.
     * @param lock An instantiated lock
     */
    public void unlock(Lock lock) {
        jdbcTemplate.queryForObject("SELECT pg_advisory_unlock(?)", Boolean.class, lock.getCode());
    }

//    /**
//     * Releases a previously-acquired exclusive session-level lock.
//     * If the lock was not held by the current session the method will still return normally.
//     * NOTE: this has a non-zero probability of collision with another string due to hashing.
//     * @param lockName A string identifying the lock
//     */
//    public void unlock(String lockName) {
//        unlock(Utils.toLong(lockName));
//    }
}
