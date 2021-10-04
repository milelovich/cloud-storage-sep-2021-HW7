package com.geekbrains;

public class PathInRequest extends Command {

    private final String dir;

    public PathInRequest(String dir) { // попадаем внутрь директории
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    @Override
    public CommandType getType() {
        return CommandType.PATH_IN_REQUEST;
    }
}