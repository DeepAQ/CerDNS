package cn.imaq.cerdns.core;

import cn.imaq.cerdns.util.ConfigUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.*;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
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
        Lookup lookup = new Lookup(reqMessage.getQuestion().getName(), reqMessage.getQuestion().getType(), reqMessage.getQuestion().getDClass());
        lookup.setResolver(ConfigUtil.getDefaultResolver());
        Record[] result = lookup.run();

        reqMessage.getHeader().setFlag(Flags.QR);
        if (result != null) {
            for (Record r : result) {
                reqMessage.addRecord(r, Section.ANSWER);
            }
        }
        try {
            channel.send(ByteBuffer.wrap(reqMessage.toWire()), client);
        } catch (IOException e) {
            log.error("Error replying to " + client, e);
        }
    }
}
