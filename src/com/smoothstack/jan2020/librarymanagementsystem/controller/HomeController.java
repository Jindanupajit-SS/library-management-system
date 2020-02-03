package com.smoothstack.jan2020.librarymanagementsystem.controller;

import com.smoothstack.jan2020.librarymanagementsystem.Repository.Repository;
import com.smoothstack.jan2020.librarymanagementsystem.templates.Menu;
import com.smoothstack.jan2020.librarymanagementsystem.templates.MenuItem;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class HomeController implements Controller {

    public String requestMapping(String endPoint, Properties model, Properties requestParam) {
        try {
            switch(endPoint) {
                case "home" :
                case "mainMenu" :
                    return Objects.requireNonNull(mainMenu(model, requestParam));
                case "processMainMenu" :
                    return  Objects.requireNonNull(processMainMenu(model, requestParam));
                case "dumpAndQuit" :
                    return Objects.requireNonNull(dumpAndQuit(model, requestParam));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String mainMenu(Properties model, Properties requestParam) {
        Menu menu = new Menu();

        menu.setBanner("Welcome to the SmoothStack Library Management System. \nWhich category of a user are you");
        menu.getMenuItemMap().put(1L, new MenuItem(menu, "Author Services"));
        menu.getMenuItemMap().put(2L, new MenuItem(menu, "Publisher Services"));
        menu.getMenuItemMap().put(3L, new MenuItem(menu, "Book Services"));
        menu.getMenuItemMap().put(4L, new MenuItem(menu, "Quit"));
        menu.getMenuItemMap().put(5L, new MenuItem(menu, "Dump HEX files and Quit"));

        Optional.ofNullable(requestParam.getProperty("error")).ifPresent(error->model.setProperty("error", error));
        Optional.ofNullable(requestParam.getProperty("info")).ifPresent(error->model.setProperty("info", error));
        model.setProperty("menu", menu.toString());
        model.setProperty("prompt", "Main Menu > ");
        model.setProperty("callback", "processMainMenu");
        return "display_menu";
    }

    public String processMainMenu(Properties model, Properties requestParam) {

        switch(Objects.requireNonNull(requestParam.getProperty("choice"))) {
            case "1": model.setProperty("redirectEndPoint", "authorMenu");
                        break;

            case "2": model.setProperty("redirectEndPoint", "publisherMenu");
                break;

            case "3": model.setProperty("redirectEndPoint", "bookMenu");
                break;

            case "4": model.setProperty("redirectEndPoint", "abort");
                break;

            case "5": model.setProperty("redirectEndPoint", "dumpAndQuit");
                break;

            default:
                model.setProperty("error", "Incorrect choice selected");
                model.setProperty("redirectEndPoint", "home");

        }

        return "redirect";
    }

    public String dumpAndQuit(Properties model, Properties requestParam) {
        Repository.dumpHEX();
        return "abort";
    }
}
