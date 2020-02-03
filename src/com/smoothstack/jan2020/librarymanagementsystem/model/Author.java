package com.smoothstack.jan2020.librarymanagementsystem.model;

import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.Entity;

public class Author implements Entity {
    private long longId;
    private String name;

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public long getLongId() {
        return longId;
    }

    public void setLongId(long longId) {
        this.longId = longId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String dump() {
        final StringBuilder sb = new StringBuilder("Author{");
        sb.append("longId=").append(longId);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
