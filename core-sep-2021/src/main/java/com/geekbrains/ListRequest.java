package com.geekbrains;

public class ListRequest extends Command{

    @Override
    public CommandType getType() {
        return CommandType.LIST_REQUEST; // возвращает клиенту список файлов на сервере
    }
}
