package com.geekbrains;

public class PathResponse extends Command{
    private final String path;

    public PathResponse(String path) {
        this.path = path;
    } // серсер отдает на клиент тот путь, на котором он счас находится

    public String getPath() {
        return path;
    }

    @Override
    public CommandType getType() {
        return CommandType.PATH_RESPONSE;
    }
}
