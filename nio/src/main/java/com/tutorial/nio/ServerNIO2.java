package com.tutorial.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Chanhui on 2014/10/15.
 */
public class ServerNIO2 {

    public static void main(String[] args) {

        try {
            final AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            asynchronousServerSocketChannel.bind(new InetSocketAddress(8080));

            asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
                    asynchronousServerSocketChannel.accept(null, this);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    socketChannel.read(byteBuffer, byteBuffer, new EchoCompletionHandler(socketChannel));
                }

                @Override
                public void failed(Throwable exc, Object attachment) {

                }
            });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
