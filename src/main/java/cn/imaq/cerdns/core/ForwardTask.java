package cn.imaq.cerdns.core;

import cn.imaq.cerdns.util.CIDR;
import cn.imaq.cerdns.util.Config;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@AllArgsConstructor
public class ForwardTask implements Runnable {
    private DatagramChannel channel;
    private SocketAddress client;
    private Message reqMessage;
    private ExecutorService queryPool;

    @Override
    public void run() {
        log.info("Query: " + reqMessage.getQuestion());
        Message result = null;
        if (reqMessage.getQuestion().getType() == Type.A) {
            log.info("> A type query, use chain");
            // Use chain
            List<Config.ChainNode> chain = Config.getChain();
            try {
                long endTime = System.currentTimeMillis() + Config.getTimeout();
                int size = chain.size();
                // Send requests
                Selector selector = Selector.open();
                DatagramChannel[] channels = new DatagramChannel[size];
                for (int i = 0; i < size; i++) {
                    DatagramChannel channel = DatagramChannel.open();
                    channels[i] = channel;
                    channel.configureBlocking(false);
                    channel.connect(chain.get(i).getServer());
                    channel.register(selector, SelectionKey.OP_READ, i);
                    channel.socket().setSoTimeout(Config.getTimeout());
                    channel.write(ByteBuffer.wrap(reqMessage.toWire()));
                }
                // Receive responses
                Message[] responses = new Message[size];
                ByteBuffer byteBuf = ByteBuffer.allocate(4096);
                int currentIndex = 0, lastIndex = 0;
                while (true) {
                    int count = selector.select(endTime - System.currentTimeMillis());
                    if (count <= 0) {
                        break;
                    }
                    Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
                    while (keyIter.hasNext()) {
                        SelectionKey key = keyIter.next();
                        keyIter.remove();
                        if (key.isReadable()) {
                            int keyIndex = (int) key.attachment();
                            byteBuf.clear();
                            ((DatagramChannel) key.channel()).read(byteBuf);
                            byteBuf.flip();
                            responses[keyIndex] = new Message(byteBuf);
                            log.info("Got result from server " + chain.get(keyIndex).getServer());
                            if (keyIndex >= lastIndex) {
                                lastIndex = keyIndex;
                                result = responses[keyIndex];
                            }
                        }
                    }
                    boolean matches = false;
                    while (currentIndex < size && responses[currentIndex] != null) {
                        Message respMessage = responses[currentIndex];
                        Record[] answer = respMessage.getSectionArray(Section.ANSWER);
                        if (answer != null && answer.length > 0) {
                            Config.ChainNode node = chain.get(currentIndex);
                            matches = true;
                            if (node.getMatchPrefixes() != null) {
                                for (Record record : answer) {
                                    if (record.getType() == Type.A) {
                                        boolean match = false;
                                        int ip = CIDR.toInt(record.rdataToString());
                                        for (byte len = 0; len < 32; len++) {
                                            if (node.getMatchPrefixes().contains(new CIDR.Prefix(ip >> (32 - len), len))) {
                                                match = true;
                                                break;
                                            }
                                        }
                                        if (!match) {
                                            matches = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (matches) {
                                log.info(node.getServer() + " result matches!");
                                result = respMessage;
                                break;
                            } else {
                                log.info(node.getServer() + " result doesn't match");
                            }
                        }
                        currentIndex++;
                    }
                    if (matches) {
                        break;
                    }
                }
                // Close channels
                for (int i = 0; i < size; i++) {
                    channels[i].disconnect();
                    channels[i].close();
                }
                selector.close();
            } catch (IOException e) {
                log.warn("Failed to forward (chain): " + e);
            }
        } else {
            SocketAddress server;
            if (reqMessage.getQuestion().getType() == Type.AAAA) {
                log.info("> AAAA type query, use v6 server");
                server = Config.getV6Server();
            } else {
                log.info("> Other type query, use default");
                server = Config.getDefaultServer();
            }
            // Query from server
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(Config.getTimeout());
                byte[] data = reqMessage.toWire();
                socket.send(new DatagramPacket(data, data.length, server));
                DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
                socket.receive(packet);
                socket.close();
                result = new Message(packet.getData());
            } catch (Exception e) {
                log.warn("Failed to forward (single): " + e);
            }
        }
        if (result != null) {
            try {
                channel.send(ByteBuffer.wrap(result.toWire()), client);
            } catch (Exception e) {
                log.error("Error replying to " + client, e);
            }
        }
    }
}
