package com.smoothstack.jan2020.librarymanagementsystem.ui;

import java.util.Properties;
import java.util.Scanner;

public interface UI {

    String uiMapping(String endPoint, Properties model, Properties requestParam);

    default String keyboardScanner(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine().trim().replaceAll("\\s+", " ");
    }

    default String keyboardScanner() {
        return keyboardScanner(" > ");
    }
}
