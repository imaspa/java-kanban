package model.httpTaskServer.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import model.TaskStatus;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GsonAdapters {

    private GsonAdapters() {
    }

    static class DurationTypeAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
        @Override
        public JsonElement serialize(final Duration src, final Type typeOfSrc, final JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Duration deserialize(final JsonElement json, final Type typeOfT,
                                    final JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull() || json.getAsString() == null || json.getAsString().isEmpty()) {
                return null;
            }

            return Duration.parse(json.getAsString());
        }
    }

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(final JsonWriter out, final LocalDateTime value) throws IOException {
            out.value(value != null ? formatter.format(value) : null);
        }

        @Override
        public LocalDateTime read(final JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }

    public static class SubtaskAdapter implements JsonSerializer<Subtask>, JsonDeserializer<Subtask> {

        @Override
        public JsonElement serialize(Subtask src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            // Сериализация полей Task
            jsonObject.addProperty("id", src.getId());
            jsonObject.addProperty("name", src.getName());
            jsonObject.addProperty("description", src.getDescription());
            jsonObject.addProperty("taskStatus", src.getTaskStatus().toString());
            jsonObject.add("startTime", context.serialize(src.getStartTime()));
            jsonObject.add("duration", context.serialize(src.getDuration()));

            // Сериализация ссылки на Epic (только ID, чтобы избежать циклических зависимостей)
            jsonObject.addProperty("epicId", src.getEpic() != null ? src.getEpic().getId() : null);

            return jsonObject;
        }

        @Override
        public Subtask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            Integer id = jsonObject.get("id").getAsInt();
            String name = jsonObject.get("id").getAsString();
            String description = jsonObject.get("id").getAsString();
            TaskStatus taskStatus = TaskStatus.valueOf(jsonObject.get("taskStatus").getAsString());

            LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
            Duration duration = context.deserialize(jsonObject.get("duration"), Duration.class);

            Epic epicObj = null;
            if (jsonObject.has("epicId") && !jsonObject.get("epicId").isJsonNull()) {
                Integer epicId = jsonObject.get("epicId").getAsInt();
                epicObj = new Epic(epicId);
            }

            Subtask subtask = new Subtask(id, name, description, taskStatus, epicObj, startTime, duration);
            return subtask;
        }
    }

    public static class EpicAdapter implements JsonSerializer<Epic>, JsonDeserializer<Epic> {
        @Override
        public JsonElement serialize(Epic src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", src.getId());
            jsonObject.addProperty("id", src.getId());
            jsonObject.addProperty("name", src.getName());
            jsonObject.addProperty("description", src.getDescription());
            jsonObject.addProperty("taskStatus", src.getTaskStatus().toString());
            jsonObject.add("startTime", context.serialize(src.getStartTime()));
            jsonObject.add("duration", context.serialize(src.getDuration()));
            jsonObject.add("endTime", context.serialize(src.getEndTime()));

            // Сериализация списка подзадач (только их ID, чтобы избежать циклических зависимостей)
            JsonArray subtasksArray = new JsonArray();
            for (Subtask subtask : src.getSubtasks()) {
                subtasksArray.add(subtask.getId());
            }
            jsonObject.add("subtasks", subtasksArray);

            return jsonObject;
        }

        @Override
        public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            Integer id = jsonObject.get("id").getAsInt();
            String name = jsonObject.get("id").getAsString();
            String description = jsonObject.get("id").getAsString();
            TaskStatus taskStatus = TaskStatus.valueOf(jsonObject.get("taskStatus").getAsString());
            LocalDateTime startTime = context.deserialize(jsonObject.get("startTime"), LocalDateTime.class);
            Duration duration = context.deserialize(jsonObject.get("duration"), Duration.class);
            Epic epic = new Epic(id, new Task(0, name, description, taskStatus, startTime, duration));
            return epic;
        }
    }


}
