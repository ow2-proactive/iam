package org.ow2.proactive.iam.exceptions;


public class IAMException extends RuntimeException {

    public IAMException(String message) {
        super(message);
    }
    public IAMException(String message, Throwable cause) {
        super(message, cause);
    }
}
