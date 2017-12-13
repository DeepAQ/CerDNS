package cn.imaq.cerdns.util;

import lombok.AllArgsConstructor;
import lombok.Data;

public class CIDR {
    public static Prefix toPrefix(String cidr) {
        String[] s = cidr.split("/");
        byte len = Byte.parseByte(s[1]);
        return new Prefix(toInt(s[0]) >> (32 - len), len);
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
        return ((addr >> (32 - prefix.len)) ^ prefix.addr) == 0;
    }

    @Data
    @AllArgsConstructor
    public static class Prefix {
        private int addr;

        private byte len;
    }
}
