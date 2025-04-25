package model;

public enum TaskType {
    TASK("Задача"),
    EPIC("Эпик"),
    SUBTASK("Подзадача");
    private final String name;

    TaskType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
