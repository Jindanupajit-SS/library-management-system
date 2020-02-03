package com.smoothstack.jan2020.librarymanagementsystem.model;

import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.Entity;

public class Branch implements Entity {

    private long longId;
    private String name;

    public Branch() {
    }

    public Branch(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public long getLongId() {
        return longId;
    }

    @Override
    public void setLongId(long longId) {
        this.longId = longId;
    }

    @Override
    public String dump() {
        final StringBuilder sb = new StringBuilder("Branch{");
        sb.append("longId=").append(longId);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
