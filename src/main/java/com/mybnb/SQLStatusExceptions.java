package com.mybnb;

import java.sql.SQLException;

import com.mybnb.request_handling.HandlerResponse;

public class SQLStatusExceptions {

    public static BaseSQLStatusException getException(SQLException e) {
        int code = e.getErrorCode();

        switch (code) {
            case 1062:
                return new DuplicateEntryException();
            case 1451:
                return new ForeignKeyDeleteException();
            case 1452:
                return new ForeignKeyFailException();
            case 404: // TODO: Change this code.
                return new NotFoundException();
            default:
                return new UnknownSQLException(e);
        }
    }

    public static class BaseSQLStatusException extends Exception {

        String message;
        Http.STATUS httpStatus;

        public BaseSQLStatusException(String message, Http.STATUS httpStatus) {
            this.message = message;
            this.httpStatus = httpStatus;
        }

        public HandlerResponse getHttpResponse() {
            return Http.MESSAGE_RESPONSE(this.message, this.httpStatus);
        }

        @Override
        public String toString() {
            return this.message;
        }
    }

    public static final class DuplicateEntryException extends BaseSQLStatusException {
        public DuplicateEntryException() {
            super("Found entry with the same key.", Http.STATUS.BAD_REQUEST);
        }
    }

    public static final class ForeignKeyDeleteException extends BaseSQLStatusException {
        public ForeignKeyDeleteException() {
            super("Cannot delete entry as it is referenced in other tables.", Http.STATUS.SERVER_ERROR);
        }
    }

    public static final class ForeignKeyFailException extends BaseSQLStatusException {
        public ForeignKeyFailException() {
            super("Cannot create/update the entry as no such foreign entry is found. Check input fields.",
                    Http.STATUS.SERVER_ERROR);
        }
    }

    public static final class NotFoundException extends BaseSQLStatusException {
        public NotFoundException() {
            super("Couldn't find such entry in the table.", Http.STATUS.NOT_FOUND);
        }
    }

    public static final class UnknownSQLException extends BaseSQLStatusException {
        public UnknownSQLException(SQLException e) {
            super(String.format("An unknown error happened (%d): %s.", e.getErrorCode(), e.getMessage()),
                    Http.STATUS.SERVER_ERROR);
        }
    }
}
