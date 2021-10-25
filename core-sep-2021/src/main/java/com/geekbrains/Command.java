package com.geekbrains;

import java.io.Serializable;

public class Command implements Serializable {

    com.geekbrains.CommandType type;

    public CommandType getType() {
        return type;
    }
}
