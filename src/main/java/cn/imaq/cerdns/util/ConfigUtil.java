package cn.imaq.cerdns.util;

import cn.imaq.cerdns.model.Config;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {
    private static Config config;

    @Getter
    private static Resolver defaultResolver;

    @Getter
    private static List<ChainNode> chain = new ArrayList<>();

    public static synchronized void loadConfig() throws Exception {
        config = new Gson().fromJson(new FileReader("config.json"), Config.class);
        // Load default resolver
        Config.Server defaultNode = config.getDefaultServer();
        defaultResolver = new SimpleResolver(defaultNode.getServer());
        defaultResolver.setPort(defaultNode.getPort());
        defaultResolver.setTimeout(config.getTimeout());
        // Load chain nodes
        for (Config.Server server : config.getChain()) {
            Resolver resolver = new SimpleResolver(server.getServer());
            resolver.setPort(server.getPort());
            resolver.setTimeout(config.getTimeout());
            chain.add(new ChainNode(resolver, config.getAddressLists().get(server.getMatchList())));
        }
    }

    @Getter
    @AllArgsConstructor
    static class ChainNode {
        private Resolver resolver;

        private List<String> matchList;
    }
}
