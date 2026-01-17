package fr.flwrian.mirror;

import fr.flwrian.config.Config.AppRoute;
import fr.flwrian.proxy.ProxyClient;
import fr.flwrian.proxy.ProxyRequest;
import fr.flwrian.proxy.ProxyResponse;

public class MirrorService {

    private final ProxyClient proxy;

    public MirrorService(ProxyClient proxy) {
        this.proxy = proxy;
    }

    public void mirror(AppRoute route, ProxyRequest req, ProxyResponse prodResp) {
        proxy.forward(route.shadow, req)
             .thenAccept(shadow -> Diff.compare(prodResp, shadow))
             .exceptionally(e -> {
                 System.err.println("Shadow error: " + e.getMessage());
                 return null;
             });
    }
}
