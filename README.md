# pglock - A Java client for distributed locking with PostgreSQL

A Java library to implement distributed locking using PostgreSQL, which leverages the well-known Java [Lock API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/Lock.html) semantics to make your code straight forward.

Distributed locks are a very useful primitive in many environments where different processes must operate with shared resources in a mutually exclusive way. 
In both distributed and monolithic systems, you often need to have things which are performed in an orderly or synchronized manner. In almost all cases where synchronization is required, it is due to the system needing to write something.  

As an example, if you have a scheduled function that performs some actions that are not idempotent, only one of your services has to execute it at a certain time, so you can acquire the distributed distributedLock at the start of the method and then perform the logic.

## About PostgreSQL distributed locks

Postgres [provides](https://www.postgresql.org/docs/current/explicit-locking.html#ADVISORY-LOCKS) simple mechanism to store that locks in database and check their state. 
Locks are fast, correct, avoid table bloat, and are automatically cleaned up by at the end of the session.  
They can be acquired at **session level** or at **transaction level**.  

### Session Level locks

Once acquired at session level, a distributedLock is held until explicitly released or the session ends. 
Session-level distributedLock requests do not honor transaction semantics: a distributedLock acquired during a transaction that is later rolled back will still be held following the rollback, and likewise an unlock is effective even if the calling transaction fails later.

### Transaction Level locks

Transaction-level distributedLock requests, on the other hand, are automatically released at the end of the transaction, and there is no explicit unlock operation.

## Guarantees

- **Mutual exclusion**: At any given moment, only one client can hold a distributedLock.
- **Deadlock free**: Eventually it is always possible to acquire a distributedLock, even if the client that locked a resource crashes without explicitly releasing it.

## Get Started

To add a dependency on `pglock-java` using Maven, use the following:

```xml
<dependency>
  <groupId>io.github.drew458</groupId>
  <artifactId>pglock-java</artifactId>
  <version>1.0.7</version>
</dependency>
```

## Usage

Once your Spring application is configured correctly to talk with a PostgreSQL database, you can start using the library like in this basic example. Note that ```tryLock(...)``` does not wait for the distributedLock to be acquired.

```java
import org.github.drew458.core.DistributedLockManager;
import org.github.drew458.model.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;

public class Main {

    private static final DistributedLock FOO_LOCK = new Lock(1234L);

    @Autowired
    private DistributedLockManager lockManager;

    private void foo() {
        boolean locked = lockManager.tryLock(FOO_LOCK);

        if (locked) {
            try {
                System.out.println("Bar");
            } finally {
                lockManager.unlock(FOO_LOCK);
            }
        }
    }
}
```

Otherwise, if you want the method to wait until the distributedLock is acquired, the code is very simple:

```java
import org.github.drew458.core.DistributedLockManager;
import org.github.drew458.model.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;

public class Main {

    private DistributedLock fooLock = new Lock(1234L);

    @Autowired
    private DistributedLockManager lockManager;

    private void foo() {
        lockManager.lock(fooLock); // the method will wait here until the distributedLock is acquired

        try {
            System.out.println("Bar");
        } finally {
            lockManager.unlock(fooLock);
        }
    }
}
```

The default lock configuration is a **mutual exclusive session lock**. Session locks are held until released or the application shuts down.  
Other configurations include:
- **Transaction level** lock: They are held until the current transaction ends; there is no need for manual release.
- **Shared** lock: A shared lock does not conflict with other shared locks on the same resource, only with exclusive locks.

### Lock keys

Since distributed locks (and other distributed synchronization primitives) are not bound to a single process, their identity is based on the key(s) provided through the constructor.  
A `DistributedLock` can be constructed in several ways:

- Passing a single `long` value.
- Passing a pair of `int` values.
- Passing a 16-character hex string (e.g. `"00000003ffffffff"`) which will be parsed as a `long`.
- Passing a pair of comma-separated 8-character hex strings (e.g. `"00000003,ffffffff"`) which will be parsed as a pair of `int`s.
- Passing an ASCII string with 0-9 characters, which will be mapped to a `long` based on a custom scheme.
- Passing an arbitrary string with the `allowHashing` option set to `true` which will be hashed to a `long`. Note that hashing will only be used if other methods of interpreting the string fail.

## Requirements

- Java 17 or newer
- Spring 3.2.6 or newer
- PostgreSQL 12 or newer

## Notes
- Make sure your application is always configured to talk to leaders and not read-only followers in the case of PostgreSQL replicated setups.

## Contributing
Contributions are welcome! Open an issue or a Pull Request to help improve the lib.
Currently, I am looking towards implementing:

- Reader-writer locks: a lock with multiple levels of access. The lock can be held concurrently either by any number of "readers" or by a single "writer".
- Semaphores: similar to a lock, but can be held by up to N users concurrently instead of just one.
- Try-with-resources pattern to improve lock handling
