package org.github.drew458.core;

import org.github.drew458.model.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractLock {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected abstract void lock(DistributedLock lock);

    protected abstract Boolean tryLock(DistributedLock lock);

    // TODO implement tryLock(long time, TimeUnit unit)

    /**
     * Releases a previously-acquired exclusive session-level distributedLock.
     * If the distributedLock was not held by the current session the method will still return normally.
     *
     * @param lock An instantiated distributedLock
     */
    public void unlock(DistributedLock lock) {

        jdbcTemplate.queryForObject(Utils.buildUnlockQuery(lock.isSingleKey(), lock.getShared()), Boolean.class, Utils.getParams(lock));
    }
}
