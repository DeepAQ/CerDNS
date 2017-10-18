package cn.imaq.cerdns.util;

import cn.imaq.cerdns.model.Config;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {
    private static Config config;

    @Getter
    private static SocketAddress defaultServer;

    @Getter
    private static List<ChainNode> chain = new ArrayList<>();

    public static int getTimeout() {
        return config.getTimeout();
    }

    public static synchronized void loadConfig() throws Exception {
        config = new Gson().fromJson(new FileReader("config.json"), Config.class);
        // Load default server
        defaultServer = new InetSocketAddress(config.getDefaultServer().getServer(), config.getDefaultServer().getPort());
        // Load chain nodes
        for (Config.Server server : config.getChain()) {
            chain.add(new ChainNode(
                    new InetSocketAddress(server.getServer(), server.getPort()),
                    config.getAddressLists().get(server.getMatchList()))
            );
        }
    }

    @Getter
    @AllArgsConstructor
    static class ChainNode {
        private SocketAddress server;

        private List<String> matchList;
    }
}
