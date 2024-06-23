# pglock - A Java client for distributed locking with PostgreSQL

A Java library to implement distributed locking using PostgreSQL, which leverages the well-known Java [Lock API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/Lock.html) semantics to make your code straight forward.

Distributed locks are a very useful primitive in many environments where different processes must operate with shared resources in a mutually exclusive way. 
Think of distributed architectures (e.g. microservices) or having multiple instances of the same service running, and you need to be sure that only one performs a certain task.  

As an example, if you have a scheduled function that performs some actions that are not idempotent, only one of your services has to execute it at a certain time, so you can acquire the distributed lock at the start of the method and then perform the logic.

## About PostgreSQL distributed locks

Postgres [provides](https://www.postgresql.org/docs/current/explicit-locking.html#ADVISORY-LOCKS) simple mechanism to store that locks in database and check their state. 
Locks are fast, correct, avoid table bloat, and are automatically cleaned up by at the end of the session.  
They can be acquired at **session level** or at **transaction level**.  

### Session Level locks

Once acquired at session level, a lock is held until explicitly released or the session ends. 
Session-level lock requests do not honor transaction semantics: a lock acquired during a transaction that is later rolled back will still be held following the rollback, and likewise an unlock is effective even if the calling transaction fails later.

### Transaction Level locks

Transaction-level lock requests, on the other hand, are automatically released at the end of the transaction, and there is no explicit unlock operation.

## Guarantees

- **Mutual exclusion**: At any given moment, only one client can hold a lock.
- **Deadlock free**: Eventually it is always possible to acquire a lock, even if the client that locked a resource crashes without explicitly releasing it.

## Get Started

To add a dependency on `pglock-java` using Maven, use the following:

```xml
<dependency>
  <groupId>io.github.drew458</groupId>
  <artifactId>pglock-java</artifactId>
  <version>1.0.4</version>
</dependency>
```

## Usage

Once your Spring application is configured correctly to talk with a PostgreSQL database, you can start using the library like in this basic example. Note that ```tryLock(...)``` does not wait for the lock to be acquired.

```java
import org.github.drew458.core.DistributedLockManager;
import org.github.drew458.core.LockManager;
import org.github.drew458.core.DistributedLockService;
import org.github.drew458.model.Lock;
import org.springframework.beans.factory.annotation.Autowired;

public class Main {

    private static final Lock FOO_LOCK = new Lock(1234L);

    @Autowired
    private DistributedLockManager lockService;

    private void foo() {
        boolean locked = lockService.tryLock(FOO_LOCK);

        if (locked) {
            try {
                System.out.println("Bar");
            } finally {
                lockService.unlock(FOO_LOCK);
            }
        }
    }
}
```

Otherwise, if you want the method to wait until the lock is acquired, the code is very simple:

```java
import org.github.drew458.core.DistributedLockManager;
import org.github.drew458.core.LockManager;
import org.github.drew458.model.Lock;
import org.springframework.beans.factory.annotation.Autowired;

public class Main {

    private static final Lock FOO_LOCK = new Lock(1234L);

    @Autowired
    private DistributedLockManager lockService;

    private void foo() {
        lockService.lock(FOO_LOCK); // the method will wait here until the lock is acquired

        try {
            System.out.println("Bar");
        } finally {
            lockService.unlock(FOO_LOCK);
        }
    }
}
```

### 

## Requirements

- Java 17 or newer
- Spring 3.2.6 or newer
- PostgreSQL 13 or newer

## Notes
- Make sure your application is always configured to talk to leaders and not read-only followers in the case of PostgreSQL replicated setups.  
