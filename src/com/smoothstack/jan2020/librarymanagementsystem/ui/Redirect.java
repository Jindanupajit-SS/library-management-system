package com.smoothstack.jan2020.librarymanagementsystem.ui;

import java.util.Objects;
import java.util.Properties;

public class Redirect implements UI {
    @Override
    public String uiMapping(String endPoint, Properties model, Properties requestParam) {
        if ("redirect".equals(endPoint)) {
            String destination = Objects.requireNonNull(model.getProperty("redirectEndPoint"));
            requestParam.clear();
            model.remove("redirectEndPoint");
            model.forEach((k,v) -> requestParam.setProperty(k.toString(),v.toString()));
            return destination;
        } else if ("abort".equals(endPoint)) {
            return "abort";
        }
        else
            return null;
    }
}
