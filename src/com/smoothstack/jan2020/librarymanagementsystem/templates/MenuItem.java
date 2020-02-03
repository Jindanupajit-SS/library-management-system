package com.smoothstack.jan2020.librarymanagementsystem.templates;

import java.io.Serializable;

public class MenuItem implements Serializable {

    private Menu menu;
    private String label;

    public MenuItem(Menu menu, String label) {
        this.menu = menu;
        this.label = label;
    }


    @Override
    public String toString() {
        return this.label;
    }


}
