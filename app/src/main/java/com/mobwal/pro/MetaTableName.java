package com.mobwal.pro;

import org.jetbrains.annotations.NotNull;

public class MetaTableName {
    public String schema;
    public String table;
    public String pKey;

    public MetaTableName(@NotNull String schema, @NotNull String table) {
        this(schema, table, "id");
    }

    public MetaTableName(@NotNull String schema, @NotNull String table, @NotNull String pKey) {
        this.schema = schema;
        this.table = table;
        this.pKey = pKey;
    }
}
