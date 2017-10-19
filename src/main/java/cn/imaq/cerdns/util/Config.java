package cn.imaq.cerdns.util;

import cn.imaq.cerdns.model.ConfigModel;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private static ConfigModel config;

    private static Map<String, List<CIDR.Prefix>> prefixes = new HashMap<>();

    @Getter
    private static SocketAddress defaultServer;

    @Getter
    private static List<ChainNode> chain = new ArrayList<>();

    public static int getTimeout() {
        return config.getTimeout();
    }

    public static synchronized void loadConfig(String filename) throws Exception {
        config = new Gson().fromJson(new FileReader(filename), ConfigModel.class);
        // Load address lists
        for (Map.Entry<String, List<String>> cidrs : config.getAddressLists().entrySet()) {
            List<CIDR.Prefix> prefixList = new ArrayList<>();
            for (String cidr : cidrs.getValue()) {
                prefixList.add(CIDR.toPrefix(cidr));
            }
            prefixes.put(cidrs.getKey(), prefixList);
        }
        // Load default server
        defaultServer = new InetSocketAddress(config.getDefaultServer().getServer(), config.getDefaultServer().getPort());
        // Load chain nodes
        for (ConfigModel.Server server : config.getChain()) {
            chain.add(new ChainNode(
                    new InetSocketAddress(server.getServer(), server.getPort()),
                    prefixes.get(server.getMatchList()))
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ChainNode {
        private SocketAddress server;

        private List<CIDR.Prefix> matchPrefixes;
    }
}
