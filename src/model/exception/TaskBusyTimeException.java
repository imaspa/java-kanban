package model.exception;

public class TaskBusyTimeException extends RuntimeException {
    public TaskBusyTimeException(String message) {
        super(message);
    }
}
