package fr.flwrian.config;

import java.io.InputStreamReader;
import java.util.List;

public class Config {
    public Server server;
    public List<AppRoute> apps;

    public void load() {
        // load config from yaml
        try {
            System.out.println("Loading config...");
            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
            InputStreamReader reader = new InputStreamReader(Config.class.getClassLoader().getResourceAsStream("config.yaml"));
            Config config = yaml.loadAs(reader, Config.class);
            server = config.server;
            apps = config.apps;
            System.out.println("Config loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }

    public static class Server {
        public int port;
    }

    public static class AppRoute {
        public String name;
        public Match match;
        public String prod;
        public String shadow;
    }

    public static class Match {
        public String host;
        public String pathPrefix;
    }
}
