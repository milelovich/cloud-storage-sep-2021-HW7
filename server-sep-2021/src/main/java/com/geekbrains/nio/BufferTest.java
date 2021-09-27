package com.geekbrains.nio;

import java.nio.ByteBuffer;
// в nio основным примитивом передачи явл каналы, а каналы работают только с буферами. Происходить в неблокирующем режиме, событий
// и каналов мб сколько угодно,
// можно читать в несколько потоковЮ получается большее кол-во перфомансов при меньшем кол-ве тредов.
// все происходит в одном треде

public class BufferTest {

    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(5); // заводим буфер на лимит 5 байт

        buffer.put((byte) 'a');
        buffer.put((byte) 'b');
        buffer.put((byte) 'c');

        buffer.flip();
        while (buffer.hasRemaining()) {
            System.out.println((char) buffer.get());
        }
        buffer.rewind(); // сбрасывает позицию: каждый пользователь пробегается по буферу геттерами и затем вызывает rewind
        while (buffer.hasRemaining()) {
            System.out.println((char) buffer.get());
        }

        buffer.clear();
        buffer.put((byte) 'd');
        buffer.put((byte) 'e');
        buffer.flip();

        while (buffer.hasRemaining()) {
            System.out.println((char) buffer.get());
        }
    }
}
