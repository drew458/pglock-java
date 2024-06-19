package org.github.drew458.core;

import org.github.drew458.model.Lock;
import org.springframework.stereotype.Service;

@Service
class SessionLock extends AbstractLock {

    /**
     * Obtains an exclusive session-level lock, waiting if necessary.
     *
     * @param lock An instantiated lock
     */
    @Override
    void lock(Lock lock) {
        jdbcTemplate.query("SELECT pg_advisory_lock(?)", rs -> null, lock.getCode());
    }

//    /**
//     * Obtains an exclusive session-level lock, waiting if necessary.
//     * NOTE: this has a non-zero probability of collision with another string due to hashing.
//     *
//     * @param lockName A string identifying the lock
//     */
//    public void lock(String lockName) {
//        lock(Utils.toLong(lockName));
//    }

    /**
     * Obtains an exclusive session-level lock if available.
     * Does not wait if the lock cannot be acquired immediately
     *
     * @param lock An instantiated lock
     * @return True if it can obtain the lock immediately, False otherwise.
     */
    @Override
    Boolean tryLock(Lock lock) {
        return jdbcTemplate.queryForObject("SELECT pg_try_advisory_lock(?)", Boolean.class, lock.getCode());
    }

//    /**
//     * Obtains an exclusive session-level lock if available.
//     * Does not wait if the lock cannot be acquired immediately
//     * NOTE: this has a non-zero probability of collision with another string due to hashing.
//     *
//     * @param lockName A string identifying the lock
//     * @return True if it can obtain the lock immediately, False otherwise.
//     */
//    public Boolean tryLock(String lockName) {
//        return tryLock(Utils.toLong(lockName));
//    }
}
