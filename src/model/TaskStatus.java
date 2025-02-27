package model;

public enum TaskStatus {
    NEW("Создана"),
    IN_PROGRESS("Ведется работа"),
    DONE("Выполнена");
    private final String name;

    TaskStatus(String name) {
        this.name = name;
    }
}
