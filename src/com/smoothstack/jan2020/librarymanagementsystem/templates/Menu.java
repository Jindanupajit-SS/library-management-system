package com.smoothstack.jan2020.librarymanagementsystem.templates;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Menu {

    private String banner = "";
    private Map<Long, MenuItem> menuItemMap;

    public Menu() {
        this.menuItemMap = new LinkedHashMap<>();;
    }

    public Menu(Map<Long, MenuItem> menuItemMap) {
        this.menuItemMap = menuItemMap;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getBanner());

        sb.append("\n");
        this.getMenuItemMap().forEach( (key,item) -> {
                sb.append("\t").append(key).append(") ").append(item.toString()).append("\n");
        });
        return sb.toString();
    }



    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public Map<Long, MenuItem> getMenuItemMap() {
        return menuItemMap;
    }

    public void setMenuItemMap(Map<Long, MenuItem> menuItemMap) {
        this.menuItemMap = menuItemMap;
    }
}
