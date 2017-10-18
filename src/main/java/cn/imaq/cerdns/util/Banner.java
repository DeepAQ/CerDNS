package cn.imaq.cerdns.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Banner {
    private static final String banner = "\n" +
            "   _____          _____  _   _  _____ \n" +
            "  / ____|        |  __ \\| \\ | |/ ____|\n" +
            " | |     ___ _ __| |  | |  \\| | (___  \n" +
            " | |    / _ \\ '__| |  | | . ` |\\___ \\ \n" +
            " | |___|  __/ |  | |__| | |\\  |____) |\n" +
            "  \\_____\\___|_|  |_____/|_| \\_|_____/ \n" +
            "  :: CerDNS Server (Alpha) ::";

    public static void print() {
        log.info(banner);
    }
}
