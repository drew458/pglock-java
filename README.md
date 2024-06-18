# pglock - A Java client for distributed locking with PostgreSQL

A Java library to implement distributed locking using [PostgreSQL](https://www.postgresql.org/docs/current/explicit-locking.html#ADVISORY-LOCKS).  

Distributed locking is useful in distributed architectures (e.g. microservices) or when you have multiple instances of the same service running, and you need to be sure that only one performs certain task.  

As an example, if you have a scheduled method and only one of your services has to execute it at a certain time, you can acquire the distributed lock at the start of the method and then perform the logic.

## About PostgreSQL distributed locks

Postgres provides simple mechanism to store that locks in database and check their state.  
Locks are fast, avoid table bloat, and are automatically cleaned up by the server at the end of the session. They can be acquired at session level or at transaction level.  

### Session Level locks

Once acquired at session level, a lock is held until explicitly released or the session ends. Session-level lock requests do not honor transaction semantics: a lock acquired during a transaction that is later rolled back will still be held following the rollback, and likewise an unlock is effective even if the calling transaction fails later.

### Transaction Level locks

Transaction-level lock requests, on the other hand, are automatically released at the end of the transaction, and there is no explicit unlock operation.

How it works, basically:

- You make request to acquire lock with some identifier in transaction A. This can be thought of as “lock the operation with id = X”
- You make some SQL requests to your database
- If any other transaction, e.g. B, will attempt to acquire lock with the same id, then it will wait until such lock will be released by A. You can specify the timeout, so transaction will wait some time and if lock won’t be unlocked B fails.
- In the end A releases lock with id = X, so it will become available to other transactions

## Requirements

- Java 8+
- Spring 3.0+

## Notes
- Make sure it is always configured to talk to leaders and not followers in the case of replicated setups.  
