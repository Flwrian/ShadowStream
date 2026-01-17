package fr.flwrian;

import fr.flwrian.app.ShadowServer;
import fr.flwrian.config.Config;

public class Main {

    public static void main(String[] args) {
        Config config = Config.load();
        ShadowServer shadow = new ShadowServer(config);
        shadow.start();
    }
}