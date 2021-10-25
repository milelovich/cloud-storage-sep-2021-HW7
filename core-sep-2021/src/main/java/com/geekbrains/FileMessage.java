package com.geekbrains;

import com.geekbrains.Command;
import com.geekbrains.CommandType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends Command {
    private final String name;
    private final long size;
    private final byte[] bytes;

    public FileMessage(Path path) throws IOException {
        name = path.getFileName().toString(); // получаем имя
        size = Files.size(path); // получаем размер
        bytes = Files.readAllBytes(path); // байты
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_MESSAGE;
    }
}
