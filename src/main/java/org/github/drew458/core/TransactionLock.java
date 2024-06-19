package org.github.drew458.core;

import org.github.drew458.model.Lock;
import org.springframework.stereotype.Service;

@Service
class TransactionLock extends AbstractLock {

    @Override
    void lock(Lock lock) {
        jdbcTemplate.query("SELECT pg_advisory_xact_lock(?)", rs -> null, lock.getCode());
    }

    @Override
    Boolean tryLock(Lock lock) {
        return jdbcTemplate.queryForObject("SELECT pg_try_advisory_xact_lock(?)", Boolean.class, lock.getCode());
    }
}
