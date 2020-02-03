package com.smoothstack.jan2020.librarymanagementsystem;

import com.smoothstack.jan2020.librarymanagementsystem.controller.*;
import com.smoothstack.jan2020.librarymanagementsystem.ui.DisplayMenu;
import com.smoothstack.jan2020.librarymanagementsystem.ui.Redirect;
import com.smoothstack.jan2020.librarymanagementsystem.ui.UI;
import com.smoothstack.jan2020.librarymanagementsystem.ui.UIInput;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class CLIApplication implements Runnable {

    public static void start() {

        Thread cli = new Thread(new CLIApplication());
        cli.setName("CLI Application");
        cli.start();
    }

    @Override
    public void run() {
        Set<Controller> controllerSet = new HashSet<>();
        Set<UI> UISet = new HashSet<>();
        String request = "home"; // Entry point
        String view = null;

        // Manually added, no component scan !!
        controllerSet.add(new HomeController());
        controllerSet.add(new BookController());
        controllerSet.add(new AuthorController());
        controllerSet.add(new PublisherController());

        UISet.add(new UIInput());
        UISet.add(new Redirect());
        UISet.add(new DisplayMenu());

        Properties model = new Properties();
        Properties requestParam = new Properties();

        // Check if all requests/UIs are mapped to controllers/views
        while (!request.equalsIgnoreCase("abort")) {


            Iterator<Controller> controller = controllerSet.iterator();

            while (view == null && controller.hasNext()) {

                view = controller.next().requestMapping(request, model, requestParam);

            }
            if (view == null) {
                System.err.printf("No request mapping for '%s'\n", request);
                System.exit(1);
            }
            requestParam.clear();
            request = null;

            Iterator<UI> ui = UISet.iterator();
            while (request == null && ui.hasNext()) {

                request = ui.next().uiMapping(view, model, requestParam);
            }
            if (request == null) {
                System.err.printf("No ui mapping for '%s'\n", view);
                System.exit(1);
            }
            view = null;
            model.clear();
        }
    }
}
