package cn.imaq.cerdns;

import cn.imaq.cerdns.core.DNSServer;
import cn.imaq.cerdns.util.Banner;
import cn.imaq.cerdns.util.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            ConfigUtil.loadConfig();
            Banner.print();
            new DNSServer(1053).start();
        } catch (Exception e) {
            log.error("Error loading config file", e);
        }
    }
}
