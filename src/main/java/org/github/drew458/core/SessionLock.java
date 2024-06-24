package org.github.drew458.core;

import org.github.drew458.model.DistributedLock;
import org.springframework.stereotype.Service;

@Service
class SessionLock extends AbstractLock {

    /**
     * Obtains an exclusive session-level lock, waiting if necessary.
     *
     * @param lock An instantiated lock
     */
    @Override
    protected void lock(DistributedLock lock) {
        jdbcTemplate.query(
                Utils.buildLockQuery(false, true, lock.isSingleKey(), lock.getShared()), rs -> null, Utils.getParams(lock));
    }

    /**
     * Obtains an exclusive session-level lock if available.
     * Does not wait if the lock cannot be acquired immediately
     *
     * @param lock An instantiated lock
     * @return True if it can obtain the lock immediately, False otherwise.
     */
    @Override
    protected Boolean tryLock(DistributedLock lock) {
        return jdbcTemplate.queryForObject(
                Utils.buildLockQuery(true, true, lock.isSingleKey(), lock.getShared()), Boolean.class, Utils.getParams(lock));
    }
}
