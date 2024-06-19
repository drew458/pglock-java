# pglock - A Java client for distributed locking with PostgreSQL

A Java library to implement distributed locking using PostgreSQL.  

Distributed locks are a very useful primitive in many environments where different processes must operate with shared resources in a mutually exclusive way. 
Think of distributed architectures (e.g. microservices) or when you have multiple instances of the same service running, and you need to be sure that only one performs a certain task.  

As an example, if you have a scheduled method and only one of your services has to execute it at a certain time, you can acquire the distributed lock at the start of the method and then perform the logic.

## About PostgreSQL distributed locks

Postgres [provides](https://www.postgresql.org/docs/current/explicit-locking.html#ADVISORY-LOCKS) simple mechanism to store that locks in database and check their state. 
Locks are fast, avoid table bloat, and are automatically cleaned up by the server at the end of the session.  
They can be acquired at session level or at transaction level.  

### Session Level locks

Once acquired at session level, a lock is held until explicitly released or the session ends. 
Session-level lock requests do not honor transaction semantics: a lock acquired during a transaction that is later rolled back will still be held following the rollback, and likewise an unlock is effective even if the calling transaction fails later.

### Transaction Level locks

Transaction-level lock requests, on the other hand, are automatically released at the end of the transaction, and there is no explicit unlock operation.

## Guarantees

- **Mutual exclusion**: At any given moment, only one client can hold a lock.
- **Deadlock free**: Eventually it is always possible to acquire a lock, even if the client that locked a resource crashes without explicitly releasing it.

## Usage

Below is a basic usage of a distributed lock usage. Note that ```tryLock(...)``` does not wait for the lock to be acquired.

```java
import org.github.drew458.core.DistributedLockingService;
import org.github.drew458.model.Lock;
import org.springframework.beans.factory.annotation.Autowired;

public class Main {
    
    private static final Lock FOO_LOCK = new Lock(1234L);

    @Autowired
    private DistributedLockingService distributedLockingService;

    private void foo() {
        boolean locked = distributedLockingService.tryLock(FOO_LOCK);

        if (locked) {
            try {
                System.out.println("Bar");
            } finally {
                distributedLockingService.unlock(FOO_LOCK);
            }
        }
    }    
}
```

## Requirements

- Java 8+
- Spring 3.0+

## Notes
- Make sure your application is always configured to talk to leaders and not read-only followers in the case of PostgreSQL replicated setups.  
