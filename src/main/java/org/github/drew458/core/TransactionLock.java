package org.github.drew458.core;

import org.github.drew458.model.DistributedLock;
import org.springframework.stereotype.Service;

@Service
class TransactionLock extends AbstractLock {

    /**
     * Obtains an exclusive transaction-level distributedLock, waiting if necessary.
     * NOTE: this method needs to be called inside a Transactional function, i.e. a method annotated with @Transactional
     *
     * @param lock An instantiated distributedLock
     */
    @Override
    protected void lock(DistributedLock lock) {
        jdbcTemplate.query(
                Utils.buildLockQuery(false, false, lock.isSingleKey()), rs -> null, Utils.getParams(lock));
    }

    /**
     * Obtains an exclusive transaction-level distributedLock if available.
     * Does not wait if the distributedLock cannot be acquired immediately
     * NOTE: this method needs to be called inside a Transactional function, i.e. a method annotated with @Transactional
     *
     * @param lock An instantiated distributedLock
     */
    @Override
    protected Boolean tryLock(DistributedLock lock) {
        return jdbcTemplate.queryForObject(
                Utils.buildLockQuery(true, false, lock.isSingleKey()), Boolean.class, Utils.getParams(lock));
    }
}
