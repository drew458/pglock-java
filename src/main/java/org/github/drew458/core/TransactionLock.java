package org.github.drew458.core;

import org.github.drew458.model.DistributedLock;
import org.springframework.stereotype.Service;

@Service
class TransactionLock extends AbstractLock {

    /**
     * Obtains an exclusive transaction-level lock, waiting if necessary.
     * NOTE: this method needs to be called inside a Transactional function, i.e. a method annotated with @Transactional
     *
     * @param lock An instantiated lock
     */
    @Override
    protected void lock(DistributedLock lock) {
        jdbcTemplate.query(
                Utils.buildLockQuery(false, false, lock.isSingleKey(), lock.getShared()), rs -> null, Utils.getParams(lock));
    }

    /**
     * Obtains an exclusive transaction-level lock if available.
     * Does not wait if the lock cannot be acquired immediately
     * NOTE: this method needs to be called inside a Transactional function, i.e. a method annotated with @Transactional
     *
     * @param lock An instantiated lock
     */
    @Override
    protected Boolean tryLock(DistributedLock lock) {
        return jdbcTemplate.queryForObject(
                Utils.buildLockQuery(true, false, lock.isSingleKey(), lock.getShared()), Boolean.class, Utils.getParams(lock));
    }
}
