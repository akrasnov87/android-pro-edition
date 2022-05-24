package ru.mobnius.core.model;

public class Claims {
    /**
     * наименование роли
     */
    final String mName;

    public Claims(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
