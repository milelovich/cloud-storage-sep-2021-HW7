package com.geekbrains.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class Server { // пишем эхо-сервер на nio

    private ServerSocketChannel serverChannel; // трансфер
    private Selector selector; // селектор
    private ByteBuffer buffer;

    private static Path ROOT = Paths.get("server-sep-2021", "root"); // хардкодим папку рут для ДЗ-2


    public Server() throws Exception {

        buffer = ByteBuffer.allocate(256); // заводим буфер
        serverChannel = ServerSocketChannel.open(); // открыли серв сокет канал
        selector = Selector.open(); // открыли селектор
        serverChannel.bind(new InetSocketAddress(8189)); //на канале привязали порт

        log.debug("Server started!");

        serverChannel.configureBlocking(false); // работаем не в блокирующем режиме
        serverChannel.register(selector, SelectionKey.OP_ACCEPT); // зарегистрировали на селекторе операцию подключения

        while (serverChannel.isOpen()) { //пока сервер открыт

            selector.select(); //селектим все события

            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = keys.iterator(); // итерируемся по этим событиям
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) { // если событие - это подключение
                    handleAccept(key);
                }
                if (key.isReadable()) { // если событие - операция чтения
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Server();
    }

    private void handleRead(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel(); // достаем из ключа канал

        buffer.clear(); // чистим буфер
        int read = 0;
        StringBuilder msg = new StringBuilder(); // заводим стринг билдер
        while (true) { // будет гоняться, пока мы все байты не вычитаем
            if (read == -1) { // канал разорван
                channel.close();
                return;
            }
            read = channel.read(buffer); // читаем буфер (пишем байты в буфер, фактически чтение - для канала, для нас - запись из сети в буфер)
            if (read == 0) { // байты не прилетают - на стриме ноль, нет ожидания байтов, как в inputStream
                break;
            }
            buffer.flip(); //записали байты, переводим в режим чтения
            while (buffer.hasRemaining()) {
                msg.append((char) buffer.get()); //в режиме чтения каждый байт засовываем в стрингбилдер
            }
            buffer.clear(); // очищаем буфер, если байты остались - будет гоняться цикл while, пока вссе байты не вычитаются
        }


        String message = msg.toString().trim(); // trim обрезает ненужные символы типа /
        log.debug("received: {}", message);
        if (message.equals("ls")) { // принимаем от пользователя команду, и если она ls
            channel.write(ByteBuffer.wrap(getFilesInfo().getBytes(StandardCharsets.UTF_8))); // отправляем пользователю в канал
        } else if (message.startsWith("cat")) { // принимаем от пользователя команду, и если она cat
            try {
                String fileName = message.split(" ")[1]; // формируем строку
                channel.write(ByteBuffer.wrap(getFileDataAsString(fileName).getBytes(StandardCharsets.UTF_8)));  // отправляем пользователю в канал
            } catch (Exception e) {
                channel.write(ByteBuffer.wrap("Command cat should be have only two args\n".getBytes(StandardCharsets.UTF_8)));
            }
        } else { // принимаем от пользователя команду, и если она не cat или ls
            channel.write(ByteBuffer.wrap("Wrong command. Use cat fileName or ls\n".getBytes(StandardCharsets.UTF_8)));
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverChannel.accept(); // делаем аксепт (принимаем)
        channel.configureBlocking(false); // на текущем принятом канале конфигурируем не блокирующий режим
        channel.register(selector, SelectionKey.OP_READ); // регистрируем операцию чтения
        log.debug("Client accepted");

    }

    // ДЗ-2
    // дописываем три функции:

    private String getFileDataAsString(String fileName) throws IOException {
        if (Files.isDirectory(ROOT.resolve(fileName))) {
            return "[ERROR] Command Cat cannot be applied to " + fileName + "\n";
        } else {
            return new String(Files.readAllBytes(ROOT.resolve(fileName))) + "\n";
        }
    }

    private String getFilesInfo() throws Exception { // функция, возвращающая информацию о файлвх в директории
        return Files.list(ROOT)
                .map(this::resolveFileType)
                .collect(Collectors.joining("\n")) + "\n"; // собираем в строку
    }

    private String resolveFileType(Path path) { // функция, сообщ информацию о том, файл это или папка
        if (Files.isDirectory(path)) {
            return String.format("%s\t%s", path.getFileName().toString(), "[DIR]");
        } else {
            return String.format("%s\t%s", path.getFileName().toString(), "[FILE]");
        }
    }
}
