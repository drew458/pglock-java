package org.github.drew458.model;

public class Lock {

public class Lock implements Closeable {

    private Long key;

    private LockType lockType = LockType.SESSION_LOCK;

    public Lock(Long key, LockType lockType) {
        this.key = key;
        this.lockType = lockType;
    }

    public Lock(Integer key1, Integer key2) {
        this.key = combineKeys(key1, key2);
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
}
