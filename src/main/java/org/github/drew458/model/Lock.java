package org.github.drew458.model;

public class Lock {

    private Long code;

    private LockType lockType = LockType.SESSION_LOCK;

    public Lock(Long code, LockType lockType) {
        this.code = code;
        this.lockType = lockType;
    }

    public Lock(Long code) {
        this.code = code;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public LockType getLockType() {
        return lockType;
    }

    public void setLockType(LockType lockType) {
        this.lockType = lockType;
    }
}
