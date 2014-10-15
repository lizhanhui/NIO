package com.tutorial.nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class Client {

    public static void main(String[] args) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            Selector selector = Selector.open();
            SocketAddress socketAddress = new InetSocketAddress("localhost", 8080);
            SocketChannel socketChannel = SocketChannel.open(socketAddress);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            ByteBuffer sendData = ByteBuffer.allocate(16);
            ByteBuffer receiveData = ByteBuffer.allocate(16);

            sendData.put("Hello NIO".getBytes());
            sendData.flip();
            socketChannel.write(sendData);

            while (true) {
                if (!socketChannel.isOpen()) {
                    break;
                }

                if(selector.select() > 0) {
                    Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    for (SelectionKey selectionKey : selectionKeySet) {
                        switch (selectionKey.readyOps()) {
                            case SelectionKey.OP_READ:
                                selector.selectedKeys().remove(selectionKey);
                                SocketChannel client = (SocketChannel)selectionKey.channel();
                                byteArrayOutputStream = new ByteArrayOutputStream();
                                receiveData.clear();
                                int count = 0;
                                while ((count = client.read(receiveData)) > 0) {
                                    if (count < receiveData.capacity()) {
                                        byte[] buffer = new byte[count];
                                        for (int i = 0; i < count; i++) {
                                            buffer[i] = receiveData.get(i);
                                        }
                                        byteArrayOutputStream.write(buffer);
                                    } else {
                                        byteArrayOutputStream.write(receiveData.array());
                                        receiveData.clear();
                                    }
                                }

                                System.out.println(new String(byteArrayOutputStream.toByteArray()));

                                //the communication is over.
                                selectionKey.cancel();
                                break;

                            default:
                                break;
                        }
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
