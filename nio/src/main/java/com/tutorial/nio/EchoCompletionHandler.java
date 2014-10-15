package com.tutorial.nio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by Chanhui on 2014/10/15.
 */
public class EchoCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    AsynchronousSocketChannel socketChannel;
    public EchoCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer result, final ByteBuffer byteBuffer) {
        byteBuffer.flip();

        socketChannel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    socketChannel.write(buffer, buffer, this);
                } else {
                    byteBuffer.compact();
                    socketChannel.read(byteBuffer, byteBuffer, EchoCompletionHandler.this);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });

    }

    @Override
    public void failed(Throwable exc, ByteBuffer buffer) {

    }
}
