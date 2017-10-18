package cn.imaq.cerdns.core;

import cn.imaq.cerdns.util.ConfigUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@AllArgsConstructor
public class ForwardTask implements Runnable {
    private DatagramChannel channel;
    private SocketAddress client;
    private Message reqMessage;
    private ExecutorService queryPool;

    @Override
    public void run() {
        Future<Message> respFuture = queryPool.submit(new QueryTask(reqMessage, ConfigUtil.getDefaultServer(), ConfigUtil.getTimeout()));
        try {
            channel.send(ByteBuffer.wrap(respFuture.get().toWire()), client);
        } catch (Exception e) {
            log.error("Error forwarding for " + client, e);
        }
    }
}
