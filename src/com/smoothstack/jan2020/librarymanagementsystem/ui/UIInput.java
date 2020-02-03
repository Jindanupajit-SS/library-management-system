package com.smoothstack.jan2020.librarymanagementsystem.ui;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class UIInput implements UI {
    @Override
    public String uiMapping(String endPoint, Properties model, Properties requestParam) {
        try {
            switch(endPoint) {
                case "string_input": return Objects.requireNonNull(stringInput(model, requestParam));
                case "yesNo": return Objects.requireNonNull(yesNo(model, requestParam));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String stringInput(Properties model, Properties requestParam) {

        Optional.ofNullable(model.remove("error")).ifPresent(System.err::println);
        Optional.ofNullable(model.remove("info")).ifPresent(System.out::println);

        Optional.ofNullable(model.remove("banner")).ifPresent(System.out::println);
        String defaultValue = (String) model.remove("default");

        if (defaultValue != null)
            System.out.printf("Enter empty string to accept default value\n\n[ %s ] ",defaultValue);

        requestParam.setProperty("input", keyboardScanner((String) Optional.ofNullable(model.remove("prompt")).orElse(" > ")));

        if (defaultValue != null && "".equals(requestParam.getProperty("input")))
            requestParam.setProperty("input", defaultValue);

        String callback = (String) Objects.requireNonNull(model.remove("callback"));

        // Preserve other variable
        model.forEach(requestParam::put);
        return callback;
    }

    public String yesNo(Properties model, Properties requestParam) {
        Optional.ofNullable(model.getProperty("error")).ifPresent(System.err::println);
        Optional.ofNullable(model.getProperty("info")).ifPresent(System.out::println);
        String banner = (String) model.remove("banner");
        String message = null;
        String prompt = (String) Optional.ofNullable(model.remove("prompt")).orElse(" (y=yes, n=no) > ");
        String callback = (String) Objects.requireNonNull(model.remove("callback"));

        // Preserve other variable
        model.forEach(requestParam::put);

        do {
            if (banner != null) System.out.println(banner);
            if (message != null) System.err.println(message);

            requestParam.setProperty("input", keyboardScanner(prompt).trim().toLowerCase());
            message = "You must enter 'y' or 'n'";

        } while (!requestParam.getProperty("input").matches("^(y|n)$"));

        return callback;
    }
}
