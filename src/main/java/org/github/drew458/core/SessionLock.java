package org.github.drew458.core;

import org.github.drew458.model.DistributedLock;
import org.springframework.stereotype.Service;

@Service
class SessionLock extends AbstractLock {

    /**
     * Obtains an exclusive session-level distributedLock, waiting if necessary.
     *
     * @param distributedLock An instantiated distributedLock
     */
    @Override
    protected void lock(DistributedLock distributedLock) {
        jdbcTemplate.query(
                Utils.buildLockQuery(false, true, distributedLock.isSingleKey()), rs -> null, Utils.getParams(distributedLock));
    }

    /**
     * Obtains an exclusive session-level distributedLock if available.
     * Does not wait if the distributedLock cannot be acquired immediately
     *
     * @param distributedLock An instantiated distributedLock
     * @return True if it can obtain the distributedLock immediately, False otherwise.
     */
    @Override
    protected Boolean tryLock(DistributedLock distributedLock) {
        return jdbcTemplate.queryForObject(
                Utils.buildLockQuery(true, true, distributedLock.isSingleKey()), Boolean.class, Utils.getParams(distributedLock));
    }
}
