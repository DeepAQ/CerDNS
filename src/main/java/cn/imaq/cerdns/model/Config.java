package cn.imaq.cerdns.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Config {
    List<Endpoint> endpoints;

    Map<String, List<String>> addressLists;

    class Endpoint {
        String server;

        int port = 53;

        String matchList;
    }
}
