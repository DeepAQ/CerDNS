package cn.imaq.cerdns.util;

import cn.imaq.cerdns.model.ConfigModel;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

public class Config {
    private static ConfigModel config;

    private static Map<String, Set<CIDR.Prefix>> prefixes = new HashMap<>();

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
            Set<CIDR.Prefix> prefixSet = new HashSet<>();
            for (String cidr : cidrs.getValue()) {
                prefixSet.add(CIDR.toPrefix(cidr));
            }
            prefixes.put(cidrs.getKey(), prefixSet);
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

        private Set<CIDR.Prefix> matchPrefixes;
    }
}
