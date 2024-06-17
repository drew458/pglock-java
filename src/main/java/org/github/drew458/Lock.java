package org.github.drew458;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class Lock {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Obtains an exclusive transaction-level advisory lock if available.
     * Does not wait if the lock cannot be acquired immediately
     * @param lockCode A 64-bit integer
     * @return true if it can obtain the lock immediately, or false if the lock cannot be acquired.
     */
    public Boolean acquireLock(Integer lockCode) {
        return jdbcTemplate.queryForObject("SELECT pg_try_advisory_lock(?)", Boolean.class, lockCode);
    }

    public void unlock(Integer lockCode) {
        jdbcTemplate.queryForObject("SELECT pg_try_advisory_unlock(?)", Boolean.class, lockCode);
    }
}
