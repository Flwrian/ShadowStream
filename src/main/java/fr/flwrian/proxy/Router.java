package fr.flwrian.proxy;

import java.util.List;

import fr.flwrian.config.Config.AppRoute;

public class Router {

    private final List<AppRoute> routes;

    public Router(List<AppRoute> routes) {
        this.routes = routes;
    }

    public AppRoute match(ProxyRequest req) {
        return routes.stream()
                .filter(r ->
                        (r.match.host == null || r.match.host.equalsIgnoreCase(req.host())) &&
                        (r.match.pathPrefix == null || req.path().startsWith(r.match.pathPrefix))
                )
                .findFirst()
                .orElse(null);
    }
}
