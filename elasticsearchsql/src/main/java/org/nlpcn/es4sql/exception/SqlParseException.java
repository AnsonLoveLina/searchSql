package org.nlpcn.es4sql.exception;

import java.sql.SQLException;

public class SqlParseException extends SQLException {

    public SqlParseException(String message) {
        super(message);
    }

    public SqlParseException(String message, Throwable throwable) {
        super(message, throwable);
    }


    private static final long serialVersionUID = 1L;

}
