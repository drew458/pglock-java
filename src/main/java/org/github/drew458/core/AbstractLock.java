package org.github.drew458.core;

import org.github.drew458.model.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractLock {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    // TODO implement tryLock(long time, TimeUnit unit)

    protected abstract void lock(DistributedLock lock);

    protected abstract Boolean tryLock(DistributedLock lock);

    /**
     * Releases a previously-acquired exclusive session-level distributedLock.
     * If the distributedLock was not held by the current session the method will still return normally.
     *
     * @param lock An instantiated distributedLock
     */
    public void unlock(DistributedLock lock) {
        jdbcTemplate.queryForObject("SELECT pg_advisory_unlock(?)", Boolean.class, lock.getKey());
    }
}
