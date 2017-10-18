package cn.imaq.cerdns;

import cn.imaq.cerdns.net.DNSServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new DNSServer(1053).start();
    }
}
