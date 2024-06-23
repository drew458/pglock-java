package org.github.drew458.core;

import org.github.drew458.model.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributedLockManager extends AbstractLock {

    @Autowired
    private SessionLock sessionLock;

    @Autowired
    private TransactionLock transactionLock;

    @Override
    public void lock(DistributedLock distributedLock) {
        switch (distributedLock.getLockType()) {
            case SESSION_LOCK: {
                sessionLock.lock(distributedLock);
                break;
            }

            case TRANSACTION_LOCK: {
                transactionLock.lock(distributedLock);
                break;
            }
        }
    }

    @Override
    public Boolean tryLock(DistributedLock distributedLock) {
        return switch (distributedLock.getLockType()) {
            case SESSION_LOCK -> sessionLock.tryLock(distributedLock);
            case TRANSACTION_LOCK -> transactionLock.tryLock(distributedLock);
        };
    }
}
