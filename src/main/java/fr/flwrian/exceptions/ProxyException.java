package fr.flwrian.exceptions;

public abstract class ProxyException extends RuntimeException {
    protected ProxyException(String message) {
        super(message);
    }
}
