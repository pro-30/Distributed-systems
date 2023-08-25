package org.example.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to decide which host to use based on round-robin algorithm
 *
 * @author prashantkumar
 */
public class BackendServers {

    private static List<String> servers = new ArrayList<>(); // list of Ip addresses of backend servers
    private static int count = 0;
    static {
        servers.add("IP1"); // replace with the backend service IP address.
        servers.add("IP2"); // replace with the backend service IP address.
    }
    public static String getHost() {
        String host = servers.get(count%servers.size());
        count++; // making it round-robin .
        return host;
    }
}
