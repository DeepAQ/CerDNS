package cn.imaq.cerdns.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class QueryTask implements Callable<Message> {
    private Message reqMessage;
    private SocketAddress server;
    private int timeout;

    @Override
    public Message call() {
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(true);
            channel.socket().setSoTimeout(timeout);
            channel.send(ByteBuffer.wrap(reqMessage.toWire()), server);
            ByteBuffer buf = ByteBuffer.allocate(4096);
            channel.receive(buf);
            channel.close();
            buf.flip();
            return new Message(buf);
        } catch (Exception e) {
            log.warn("Error querying to " + server, e);
            return null;
        }
    }
}
