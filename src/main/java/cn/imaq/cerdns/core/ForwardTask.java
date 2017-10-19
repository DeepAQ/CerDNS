package cn.imaq.cerdns.core;

import cn.imaq.cerdns.util.CIDR;
import cn.imaq.cerdns.util.Config;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
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
        log.info("Query: " + reqMessage.getQuestion());
        Message result = null;
        if (reqMessage.getQuestion().getType() == Type.A) {
            log.info("A type query, use chain");
            // Use chain
            List<Config.ChainNode> chain = Config.getChain();
            List<Future<Message>> futures = new ArrayList<>();
            Message fallback = null;
            for (Config.ChainNode node : chain) {
                futures.add(queryPool.submit(new QueryTask(reqMessage, node.getServer(), Config.getTimeout())));
            }
            for (int i = 0; i < futures.size(); i++) {
                Future<Message> future = futures.get(i);
                log.info("Getting result from server " + chain.get(i).getServer());
                try {
                    Message respMessage = future.get();
                    if (respMessage == null) {
                        continue;
                    }
                    fallback = respMessage;
                    Record[] answer = respMessage.getSectionArray(Section.ANSWER);
                    if (answer != null) {
                        Config.ChainNode node = chain.get(i);
                        boolean matches = true;
                        if (node.getMatchPrefixes() != null) {
                            for (Record record : answer) {
                                if (record.getType() == Type.A) {
                                    boolean match = false;
                                    for (CIDR.Prefix prefix : node.getMatchPrefixes()) {
                                        if (CIDR.match(record.rdataToString(), prefix)) {
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
                            log.info("Result matches!");
                            result = respMessage;
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            if (result == null) {
                log.info("Null result, use fallback instead");
                result = fallback;
            }
        } else {
            log.info("Other type query, use default");
            // Query from default server
            Future<Message> respFuture = queryPool.submit(new QueryTask(reqMessage, Config.getDefaultServer(), Config.getTimeout()));
            try {
                result = respFuture.get();
            } catch (Exception ignored) {
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
