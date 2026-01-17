package fr.flwrian.exceptions;

public class Exceptions extends Exception {
    
    public class ProdTimeoutException extends RuntimeException {}
    public class ProdUnreachableException extends RuntimeException {}
    public class ProxyInternalException extends RuntimeException {}

}
