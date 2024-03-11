package ru.practicum.shareit.exception;

public class DataBadRequestException extends RuntimeException {

    private String errorMessage;
    public DataBadRequestException(String errorMessage, String description) {
        super(description);
        this.errorMessage = errorMessage;
    }

    public DataBadRequestException(String description) {
        super(description);
        errorMessage = "Error";
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
