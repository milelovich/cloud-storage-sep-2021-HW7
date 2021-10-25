package com.geekbrains.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

// send string принимает строки
// receive string отдает строки
@Slf4j
public class NettyEchoServer {

    public NettyEchoServer() {

        EventLoopGroup auth = new NioEventLoopGroup(1); // создаем тред-пулы (указ кол-во тредов)
        EventLoopGroup worker = new NioEventLoopGroup(); // заберет все треды

        try {
            ServerBootstrap bootstrap = new ServerBootstrap(); // формируем сервер бутстрап, будем навешивать всякие св-ва

            ChannelFuture channelFuture = bootstrap.group(auth, worker) // передали в группу
                    .channel(NioServerSocketChannel.class) // каким образом будет идти сетевой трансфер
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast( // pipeline - конвеер обработчиков,
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new FileMessageHandler()
                            );
                        }
                    })
                    .bind(8189) // биндим на порт
                    .sync(); // запускаем
            log.debug("Server started...");
            channelFuture.channel().closeFuture().sync(); // block, блокирующая операция - в этом месте мы подвиснем, генерит вайл-тру цикл
        }catch (Exception e) {
            log.error("Server exception: Stacktrace: ", e);
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyEchoServer();
    }

}
