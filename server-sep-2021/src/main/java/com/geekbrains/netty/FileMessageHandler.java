package com.geekbrains.netty;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import com.geekbrains.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileMessageHandler extends SimpleChannelInboundHandler<Command> {

    private static Path currentPath;

    public FileMessageHandler() throws IOException {
        currentPath = Paths.get("server", "root");
        if (!Files.exists(currentPath)) { // создаем директорию, если ее не существует
            Files.createDirectory(currentPath);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListResponse(currentPath)); // подключились, отправляем список файлов на сервере
        ctx.writeAndFlush(new PathResponse(currentPath.toString())); // отправляем, где мы сейчас на сервере
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        log.debug("received: {}", command.getType());
        switch (command.getType()) { // что за команду получили:
            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) command;
                FileMessage msg = new FileMessage(currentPath.resolve(fileRequest.getName())); // из файла собирается файл-месседж
                ctx.writeAndFlush(msg); // отправляется в контекст
                break;
            case FILE_MESSAGE: // если на сервер летит файл-месседж
                FileMessage message = (FileMessage) command; // преобразуем команду к файл-месседжу
                Files.write(currentPath.resolve(message.getName()), message.getBytes()); // пересоздает ресурс, записывает файл на сервер
                ctx.writeAndFlush(new ListResponse(currentPath)); // шлем в контекст лист-респонс, тк файлов на сервере стало больше
                break;
            case LIST_REQUEST:
                ctx.writeAndFlush(new ListResponse(currentPath)); // посылаем список файлов на сервере
                break;
            case PATH_UP_REQUEST: // если у текущего пути есть родительский
                if (currentPath.getParent() != null) {
                    currentPath = currentPath.getParent();
                }
                ctx.writeAndFlush(new PathResponse(currentPath.toString()));
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;
            case PATH_IN_REQUEST:
                PathInRequest request = (PathInRequest) command;
                Path newPath = currentPath.resolve(request.getDir());
                if (Files.isDirectory(newPath)) { // если это директория
                    currentPath = newPath; // открываем, присваеваем новый путь
                    ctx.writeAndFlush(new PathResponse(currentPath.toString()));
                    ctx.writeAndFlush(new ListResponse(currentPath));
                }
                break;
        }
    }
}
