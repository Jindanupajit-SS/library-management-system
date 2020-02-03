package com.smoothstack.jan2020.librarymanagementsystem.model;

import com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine.Entity;

public class Publisher implements Entity {

    private long longId;
    private String name;
    private String address;

    public Publisher() {
    }

    public Publisher(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Publisher(String name) {
        this.name = name;
        this.address = "";
    }

    @Override
    public long getLongId() {
        return longId;
    }

    @Override
    public void setLongId(long longId) {
        this.longId = longId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String dump() {
        final StringBuilder sb = new StringBuilder("Publisher{");
        sb.append("longId=").append(longId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
