package cn.imaq.cerdns;

import cn.imaq.cerdns.core.DNSServer;
import cn.imaq.cerdns.util.Banner;
import cn.imaq.cerdns.util.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            if (args.length == 0) {
                Config.loadConfig("config.json");
            } else {
                Config.loadConfig(args[0]);
            }
            Banner.print();
            new DNSServer(53).start();
        } catch (Exception e) {
            log.error("Error loading config file", e);
        }
    }
}
