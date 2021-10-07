package com.geekbrains.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoHandler extends SimpleChannelInboundHandler<String> {

    private static int cnt = 0;
    private String name;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // когда клиент подключился нетти будет дергать эту функцию
        cnt++;
        name = "user#" + cnt;
        log.debug("Client {} connected!", name);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception { // от пользователя прилетело сообщение
        log.debug("Received: {}", s);
        ctx.writeAndFlush(String.format("[%s]: %s", name, s));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // клиент отключился
        log.debug("Client {} disconnected", name);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("", cause);
    }
}
