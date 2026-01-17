package fr.flwrian.exceptions;

public class ProdTimeoutException extends ProxyException {
    public ProdTimeoutException() {
        super("Production backend timed out");
    }
}
