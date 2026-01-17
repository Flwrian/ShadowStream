package fr.flwrian;

import fr.flwrian.config.Config;
import fr.flwrian.server.Shadow;

public class Main {

    public static void main(String[] args) {
        Config config = Config.load();
        Shadow shadow = new Shadow(config);
        shadow.start();
    }
}