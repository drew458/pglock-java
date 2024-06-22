package org.github.drew458.admin;

import org.github.drew458.model.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributedLockInfo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Releases all session-level advisory locks held by the current session.
     */
    public void unlockAll() {
        jdbcTemplate.query("SELECT pg_advisory_unlock_all()", rse -> null);
    }

    /**
     * Queries if this lock is held by any session.
     * This method is designed for use in monitoring of the system state, not for synchronization control.
     *
     * @param lock An instantiated lock
     * @return true if any session holds this lock and false otherwise
     */
    public Boolean isLocked(Lock lock) {
        return jdbcTemplate.queryForObject("SELECT EXISTS ( " +
                        "SELECT objid FROM pg_catalog.pg_locks WHERE locktype = 'advisory' AND CAST(objid AS bigint) = ?" +
                        ")",
                Boolean.class, lock);
    }

    /**
     * Retrieves all the distributed locks currently held in the database by any session.
     *
     * @return A list of Locks
     */
    public List<Lock> getAllLocks() {
        return jdbcTemplate.query("SELECT CAST(objid AS bigint) FROM pg_locks WHERE locktype = 'advisory'",
                (rs, rowNum) -> new Lock(rs.getLong(1)));
    }
}
