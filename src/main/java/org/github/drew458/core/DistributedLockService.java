package org.github.drew458.core;

import org.github.drew458.model.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributedLockService extends AbstractLock {

    @Autowired
    private SessionLock sessionLock;

    @Autowired
    private TransactionLock transactionLock;

    @Override
    public void lock(Lock lock) {
        switch (lock.getLockType()) {
            case SESSION_LOCK: {
                sessionLock.lock(lock);
                break;
            }

            case TRANSACTION_LOCK: {
                transactionLock.lock(lock);
                break;
            }
        }
    }

    @Override
    public Boolean tryLock(Lock lock) {
        return switch (lock.getLockType()) {
            case SESSION_LOCK -> sessionLock.tryLock(lock);
            case TRANSACTION_LOCK -> transactionLock.tryLock(lock);
        };
    }
}
