package org.windwant.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by aayongche on 2016/9/12.
 */
public class AIOServer implements Runnable{

    private int port = 8889;
    private int threadSize = 10;
    public AIOServer(int port, int threadSize) {
        this.port = port;
        this.threadSize = threadSize;
    }

    public static void main(String[] args) throws IOException {
        new Thread(new AIOServer(8989, 19)).start();
    }

    public void run() {
        try{
            AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), 10);
            AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(asynchronousChannelGroup);
            serverChannel.bind(new InetSocketAddress(port));
            System.out.println("listening on port: " + port);
            serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                final ByteBuffer echoBuffer = ByteBuffer.allocateDirect(1024);

                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    System.out.println("reading begin...");
                    echoBuffer.clear();
                    result.read(echoBuffer);
                    echoBuffer.flip();
                    System.out.println("received : " + Charset.defaultCharset().decode(echoBuffer));
                    System.out.println("server send data:");
                    result.write(ByteBuffer.wrap("server test data: ".getBytes()));

                }

                public void failed(Throwable exc, Object attachment) {
                    System.out.println("received failed");
                }
            });
            System.in.read();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
