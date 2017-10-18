package cn.imaq.cerdns.util;

import lombok.AllArgsConstructor;

public class CIDR {
    public static Prefix toPrefix(String cidr) {
        String[] s = cidr.split("/");
        return new Prefix(toInt(s[0]), Byte.parseByte(s[1]));
    }

    public static int toInt(String ip) {
        String[] sects = ip.split("\\.");
        int addr = 0;
        for (String sect : sects) {
            addr = addr * 256 + Integer.parseInt(sect);
        }
        return addr;
    }

    public static boolean match(String ip, Prefix prefix) {
        int addr = toInt(ip);
        return ((addr ^ prefix.addr) >> (32 - prefix.len)) == 0;
    }

    @AllArgsConstructor
    public static class Prefix {
        private int addr;

        private byte len;
    }
}
