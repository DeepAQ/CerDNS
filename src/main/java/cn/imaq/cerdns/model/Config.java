package cn.imaq.cerdns.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Config {
    private int timeout = 2000;

    private Server defaultServer;

    private List<Server> chain;

    private Map<String, List<String>> addressLists = new HashMap<>();

    @Getter
    public class Server {
        private String server;

        private int port = 53;

        private String matchList;
    }
}
