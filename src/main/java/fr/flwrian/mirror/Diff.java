package fr.flwrian.mirror;

import java.util.Arrays;

import fr.flwrian.proxy.ProxyResponse;

public class Diff {

    public static void compare(ProxyResponse prod, ProxyResponse shadow) {
        if (prod.status() != shadow.status()) {
            System.out.println("DIFF STATUS prod=" + prod.status()
                    + " shadow=" + shadow.status());
        }
        if (!Arrays.equals(prod.body(), shadow.body())) {
            System.out.println("DIFF BODY");
        }
    }
}
