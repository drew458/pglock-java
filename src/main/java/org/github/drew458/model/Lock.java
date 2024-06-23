package org.github.drew458.model;

import java.io.Closeable;

public class Lock implements Closeable {

    private Long key;

    private LockType lockType = LockType.SESSION_LOCK;

    public Lock(Long key, LockType lockType) {
        this.key = key;
        this.lockType = lockType;
    }

    public Lock(Long key) {
        this.key = key;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public LockType getLockType() {
        return lockType;
    }

    public void setLockType(LockType lockType) {
        this.lockType = lockType;
    }

    @Override
    public void close() {
        //TODO
    }
}
