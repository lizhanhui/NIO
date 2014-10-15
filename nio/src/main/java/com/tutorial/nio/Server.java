package com.tutorial.nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class Server {

    public static void main(String[] args) {

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 8080));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            new Thread(new ReactorTask(selector)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ReactorTask implements Runnable {

    private final Selector selector;


    public ReactorTask(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        ByteArrayOutputStream byteArrayOutputStream = null;
        while (true) {
            try {
                int num = selector.select();
                if (num > 0) {
                    Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    SocketChannel socketChannel = null;
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    for (SelectionKey selectionKey : selectionKeySet) {
                        SelectableChannel selectableChannel = selectionKey.channel();
                        selector.selectedKeys().remove(selectionKey);
                        switch (selectionKey.readyOps()) {
                            case SelectionKey.OP_ACCEPT:
                                socketChannel = ((ServerSocketChannel)selectableChannel).accept();
                                socketChannel.configureBlocking(false);
                                socketChannel.socket().setReuseAddress(true);
                                socketChannel.register(selector, SelectionKey.OP_READ);
                                break;

                            case SelectionKey.OP_READ:
                                socketChannel = (SocketChannel)selectableChannel;
                                 byteArrayOutputStream = new ByteArrayOutputStream();
                                byteBuffer.clear();
                                int read = 0;
                                while ((read = socketChannel.read(byteBuffer)) != -1) {
                                    if (read < byteBuffer.capacity()) {
                                        byte[] cache = new byte[read];
                                        for (int i = 0; i < read; i++) {
                                            cache[i] = byteBuffer.get(i);
                                        }
                                        byteArrayOutputStream.write(cache);
                                        break;
                                    } else {
                                        byteArrayOutputStream.write(byteBuffer.array());
                                    }
                                }
                                byteBuffer.clear();

                                byte[] data = byteArrayOutputStream.toByteArray();
                                byteArrayOutputStream.flush();
                                byteArrayOutputStream.close();
                                String header = "Response: ";
                                ByteBuffer writeBackData = ByteBuffer.allocate(header.length() + data.length);
                                writeBackData.put(header.getBytes()).put(data).flip();
                                socketChannel.write(writeBackData);
                                System.out.println(new String(data));
                                socketChannel.close();

                                //The communication is over.
                                selectionKey.cancel();
                                break;
                            default:
                                break;
                        }

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != byteArrayOutputStream) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e) {
                        //ignore
                    }
                }
            }
        }
    }
}