package org.github.drew458.core;

import org.github.drew458.model.DistributedLock;

public class Utils {

    protected static String buildLockQuery(boolean isWait, boolean isSession, boolean isSingleKey, boolean isShared) {

        var sb = new StringBuilder("SELECT pg_catalog.pg_");

        if (!isWait) {
            sb.append("try_");
        }

        sb.append("advisory_");

        if (!isSession) {
            sb.append("xact_");
        }

        sb.append("lock");

        if (isShared) {
            sb.append("_shared");
        }

        if (isSingleKey) {
            sb.append("(?)");
        } else {
            sb.append("(?,?)");
        }

        return sb.toString();
    }

    protected static String buildUnlockQuery(boolean isSingleKey, boolean isShared) {

        var sb = new StringBuilder("SELECT pg_catalog.pg_advisory_unlock");

        if (isShared) {
            sb.append("_shared");
        }

        if (isSingleKey) {
            sb.append("(?)");
        } else {
            sb.append("(?,?)");
        }

        return sb.toString();
    }

    protected static Object[] getParams(DistributedLock lock) {
        Object[] params;

        if (lock.isSingleKey()) {
            params = new Object[]{lock.getKey()};
        } else {
            var keys = lock.getKeys();
            params = new Object[]{keys.get(0), keys.get(1)};
        }

        return params;
    }
}
