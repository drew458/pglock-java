package org.github.drew458;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class Lock {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtains an exclusive session-level advisory lock, waiting if necessary.
     * @param lockCode A 64-bit integer
     */
    public void lock(Integer lockCode) {
         jdbcTemplate.query("SELECT pg_advisory_lock(?)", rs -> null, lockCode);
    }

    /**
     * Obtains an exclusive session-level advisory lock if available.
     * Does not wait if the lock cannot be acquired immediately
     * @param lockCode A 64-bit integer
     * @return True if it can obtain the lock immediately, False otherwise.
     */
    public Boolean tryLock(Integer lockCode) {
        return jdbcTemplate.queryForObject("SELECT pg_try_advisory_lock(?)", Boolean.class, lockCode);
    }

    // TODO implement tryLock(long time, TimeUnit unit)

    public void unlock(Integer lockCode) {
        jdbcTemplate.queryForObject("SELECT pg_advisory_unlock(?)", Boolean.class, lockCode);
    }
}
