package com.geekbrains.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Handler implements Runnable {

    private static final int BUFFER_SIZE = 256; // заводим буфер на сервере, не обязательно такого же размера, что и на клиенте
    private final String ROOT_DIR = "server-sep-2021/root"; // определяем директорию
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private final Socket socket;
    private DataOutputStream os;
    private DataInputStream is;

    public Handler(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @SneakyThrows // анотация, дописывающая try/catch
    @Override
    public void run() {
        try  {
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());

            //начинаем отправку
            while (true) {
                String fileName = is.readUTF(); // сервер ждет имя файла
                log.debug("Received fileName: {}", fileName);
                long size = is.readLong();// сервер ждет размер файла
                log.debug("File size: {}", size);
                int read;
                try (OutputStream fos = Files.newOutputStream(Paths.get(ROOT_DIR, fileName))) { // поднимаем OutputStream, и где (Paths.get(ROOT_DIR, fileName)
                    for (int i = 0; i < (size + BUFFER_SIZE - 1) / BUFFER_SIZE; i++) { // итерации для разного количества передаваемых байтов
                        read = is.read(buffer); // сколько байт прочитано в буфер
                        fos.write(buffer, 0 , read);
                    }
                } catch (Exception e) {
                    log.error("problem with file system");
                }
                os.writeUTF("OK"); // если все ок
            }
        } catch (Exception e) {
            log.error("stacktrace: ", e);
            os.writeUTF("Error! " + e.getMessage()); // логируем исключения
        }
        is.close();
        os.close();
    }
}
