package org.github.drew458.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionLocking {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtains an exclusive session-level lock, waiting if necessary.
     * @param lockCode A 64-bit integer
     */
    public void lock(Long lockCode) {
         jdbcTemplate.query("SELECT pg_advisory_lock(?)", rs -> null, lockCode);
    }

    /**
     * Obtains an exclusive session-level lock, waiting if necessary.
     * NOTE: this has a non-zero probability of collision with another string due to hashing.
     * @param lockName A string identifying the lock
     */
    public void lock(String lockName){
        lock(Utils.toLong(lockName));
    }

    /**
     * Obtains an exclusive session-level lock if available.
     * Does not wait if the lock cannot be acquired immediately
     * @param lockCode A 64-bit integer
     * @return True if it can obtain the lock immediately, False otherwise.
     */
    public Boolean tryLock(Long lockCode) {
        return jdbcTemplate.queryForObject("SELECT pg_try_advisory_lock(?)", Boolean.class, lockCode);
    }

    /**
     * Obtains an exclusive session-level lock if available.
     * Does not wait if the lock cannot be acquired immediately
     * NOTE: this has a non-zero probability of collision with another string due to hashing.
     * @param lockName A string identifying the lock
     * @return True if it can obtain the lock immediately, False otherwise.
     */
    public Boolean tryLock(String lockName) {
        return tryLock(Utils.toLong(lockName));
    }

    // TODO implement tryLock(long time, TimeUnit unit)

    /**
     * Releases a previously-acquired exclusive session-level lock.
     * If the lock was not held by the current session the method will still return normally.
     * @param lockCode A 64-bit integer
     */
    public void unlock(Long lockCode) {
        jdbcTemplate.queryForObject("SELECT pg_advisory_unlock(?)", Boolean.class, lockCode);
    }

    /**
     * Releases a previously-acquired exclusive session-level lock.
     * If the lock was not held by the current session the method will still return normally.
     * NOTE: this has a non-zero probability of collision with another string due to hashing.
     * @param lockName A string identifying the lock
     */
    public void unlock(String lockName) {
        unlock(Utils.toLong(lockName));
    }
}
