package com.smoothstack.jan2020.librarymanagementsystem.ui;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class DisplayMenu implements UI {
    @Override
    public String uiMapping(String endPoint, Properties model, Properties requestParam) {
        try {
            switch(endPoint) {
                case "display_menu": return Objects.requireNonNull(displayMenu(model, requestParam));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String displayMenu(Properties model, Properties requestParam) {

        Optional.ofNullable(model.getProperty("error")).ifPresent(System.err::println);
        Optional.ofNullable(model.getProperty("info")).ifPresent(System.out::println);
        System.out.println(model.getProperty("menu"));
        requestParam.setProperty("choice", keyboardScanner(model.getProperty("prompt")));
        return model.getProperty("callback");
    }
}
