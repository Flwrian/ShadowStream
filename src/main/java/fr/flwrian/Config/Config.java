package fr.flwrian.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class Config {
    public Server server;
    public List<AppRoute> apps;

    public static Config load() {
        try {
            Yaml yaml = new Yaml();
            InputStreamReader reader =
                new InputStreamReader(
                    Objects.requireNonNull(
                        Config.class.getClassLoader().getResourceAsStream("config.yml"),
                        "config.yml not found"
                    )
                );
            return yaml.loadAs(reader, Config.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
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

        public boolean shouldMirror(AppRoute route) {
            return route.shadow != null && !route.shadow.isEmpty();
        }

    }

    public static class Match {
        public String host;
        public String pathPrefix;
    }
}
