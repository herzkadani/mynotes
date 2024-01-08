package ch.bbzbl.mynotes.security.exceptions;

public class BadInputException extends Exception{

    public BadInputException() {
        super();
    }


    public BadInputException(String message) {
        super(message);
    }


    public BadInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
