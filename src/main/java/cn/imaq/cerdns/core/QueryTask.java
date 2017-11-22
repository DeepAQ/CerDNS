package cn.imaq.cerdns.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
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
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(timeout);
            byte[] data = reqMessage.toWire();
            socket.send(new DatagramPacket(data, data.length, server));
            data = new byte[4096];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            socket.close();
            return new Message(data);
        } catch (Exception e) {
            log.warn("Error querying to " + server, e);
            return null;
        }
    }
}
