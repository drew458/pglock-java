package org.github.drew458.core;

import org.github.drew458.model.Lock;

public class Utils {

    protected static String buildLockQuery(boolean isWait, boolean isSession, boolean isSingleKey) {

        var sb = new StringBuilder("SELECT pg_catalog.pg_");

        if (!isWait) {
            sb.append("try_");
        }
        sb.append("advisory_");
        if (!isSession) {
            sb.append("xact_");
        }
        sb.append("lock");
        if (isSingleKey) {
            sb.append("(?)");
        } else {
            sb.append("(?,?)");
        }

        return sb.toString();
    }

    protected static Object[] getParams(Lock lock) {
        Object[] params;

        if (lock.hasSingleKey()) {
            params = new Object[]{lock.getKey()};
        } else {
            var keys = lock.getKeys();
            params = new Object[]{keys.get(0), keys.get(1)};
        }

        return params;
    }
}
