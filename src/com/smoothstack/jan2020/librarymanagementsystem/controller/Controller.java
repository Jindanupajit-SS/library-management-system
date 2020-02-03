package com.smoothstack.jan2020.librarymanagementsystem.controller;

import java.util.Properties;

public interface Controller {
    public String requestMapping(String endPoint, Properties model, Properties requestParam);
}
