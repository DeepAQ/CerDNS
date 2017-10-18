package cn.imaq.cerdns.net;

import cn.imaq.cerdns.util.Banner;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

@Slf4j
public class DNSServer {
    private int port = 53;

    private volatile boolean running = false;

    public DNSServer() {
    }

    public DNSServer(int port) {
        this.port = port;
    }

    public synchronized void start() throws IOException {
        if (running) {
            return;
        }
        Banner.print();
        // Start channel
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(true);
        channel.socket().bind(new InetSocketAddress(port));
        running = true;
        new ServerThread(channel).start();
    }

    public void stop() {
        running = false;
    }

    class ServerThread extends Thread {
        private final DatagramChannel channel;
        private final ByteBuffer buf = ByteBuffer.allocateDirect(1024);

        ServerThread(DatagramChannel channel) {
            super("UDP-Server");
            this.channel = channel;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    buf.clear();
                    SocketAddress src = channel.receive(buf);
                    buf.flip();
                    if (src != null) {
                        log.info("Received packet from " + src);
                        Message message = new Message(buf);
                        log.info(String.valueOf(message));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
