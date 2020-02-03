package com.smoothstack.jan2020.librarymanagementsystem.DatabaseEngine;


import java.io.Serializable;

public interface Entity extends Serializable, HasLongId {

    String dump();
}
