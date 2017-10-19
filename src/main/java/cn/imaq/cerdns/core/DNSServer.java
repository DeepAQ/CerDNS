package cn.imaq.cerdns.core;

import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;
import org.xbill.DNS.Opcode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DNSServer {
    private int port;
    private ExecutorService workers = Executors.newCachedThreadPool();
    private volatile boolean running = false;

    public DNSServer(int port) {
        this.port = port;
    }

    public synchronized void start() throws IOException {
        if (running) {
            return;
        }
        // Start channel
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(true);
        channel.socket().bind(new InetSocketAddress(port));
        running = true;
        // Start server thread
        new ServerThread(channel).start();
        log.info("DNS server started");
    }

    public void stop() {
        running = false;
    }

    class ServerThread extends Thread {
        private final DatagramChannel channel;
        private final ByteBuffer buf = ByteBuffer.allocateDirect(4096);

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
                        log.debug("Received datagram from " + src);
                        Message message = new Message(buf);
                        if (message.getHeader().getOpcode() == Opcode.QUERY) {
                            // Dispatch
                            workers.execute(new ForwardTask(channel, src, message, workers));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
