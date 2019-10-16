package org.nlpcn.es4sql.exception;

public class SqlParseException extends Exception {

    public SqlParseException(String message) {
        super(message);
    }

    public SqlParseException(String message, Throwable cause) {
        super(message, cause);
    }


    private static final long serialVersionUID = 1L;

}
