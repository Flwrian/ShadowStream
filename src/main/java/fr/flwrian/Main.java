package fr.flwrian;

import fr.flwrian.server.Shadow;

public class Main {

    public static void main(String[] args) {

        // try to load config
        Shadow shadow = new Shadow();
        shadow.start();
    }
}