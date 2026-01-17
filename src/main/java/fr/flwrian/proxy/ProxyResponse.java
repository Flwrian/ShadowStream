package fr.flwrian.proxy;

import java.util.List;
import java.util.Map;

public record ProxyResponse(
        int status,
        Map<String, List<String>> headers,
        byte[] body
) {}
