package com.darksoldier1404.dppc.data.sql;

import com.darksoldier1404.dppc.annotation.DPPCoreVersion;

/**
 * Supported database types for
 * {@link com.darksoldier1404.dppc.data.sql.DBDataContainer}.
 */
@DPPCoreVersion(since = "5.4.0")
public enum DBType {
    /** MySQL / MariaDB */
    MYSQL,
    /** SQLite (file-based, embedded) */
    SQLITE
}


