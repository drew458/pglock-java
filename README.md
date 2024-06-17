# pglock - A Java client for distributed locking in PostgreSQL

PostgreSQL's backed distributed lock.  
Make sure it is always configured to talk to leaders and not followers in the case of replicated setups.  

Postgres provides simple mechanism to store that locks in database and check their state. 
Advisory locks are faster, avoid table bloat, and are automatically cleaned up by the server at the end of the session.  

Advisory locks can be acquired at session level or at transaction level.

Once acquired at session level, an advisory lock is held until explicitly released or the session ends. Unlike standard lock requests, session-level advisory lock requests do not honor transaction semantics: a lock acquired during a transaction that is later rolled back will still be held following the rollback, and likewise an unlock is effective even if the calling transaction fails later.

Transaction-level lock requests, on the other hand, behave more like regular lock requests: they are automatically released at the end of the transaction, and there is no explicit unlock operation.

How it works, basically:

- You make request to acquire lock with some identifier in transaction A. This can be thought of as “lock the operation with id = X”
- You make some SQL requests to your database
- If any other transaction, e.g. B, will attempt to acquire lock with the same id, then it will wait until such lock will be released by A. You can specify the timeout, so transaction will wait some time and if lock won’t be unlocked B fails.
- In the end A releases lock with id = X, so it will become available to other transactions

fd