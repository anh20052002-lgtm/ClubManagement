package clubmanagement.repository;

import clubmanagement.exception.DataAccessException;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EventRepository {
    private static final String HEADER = "id,name,eventDate,location,description,maxParticipants,status";

    private final ArrayList<Event> events;
    private final String filePath;

    public EventRepository() throws DataAccessException {
        this("data/events.csv");
    }

    public EventRepository(String filePath) throws DataAccessException {
        this.filePath = filePath;
        this.events = new ArrayList<>();
        loadFromFile();
    }

    public void add(Event event) throws DataAccessException {
        events.add(event);
        saveToFile();
    }

    public ArrayList<Event> findAll() {
        return new ArrayList<>(events);
    }

    public Event findById(String id) {
        if (id == null) {
            return null;
        }

        for (Event event : events) {
            if (event.getId().equalsIgnoreCase(id)) {
                return event;
            }
        }

        return null;
    }

    public boolean update(Event updatedEvent) throws DataAccessException {
        if (updatedEvent == null) {
            return false;
        }

        for (int i = 0; i < events.size(); i++) {
            Event currentEvent = events.get(i);

            if (currentEvent.getId().equalsIgnoreCase(updatedEvent.getId())) {
                events.set(i, updatedEvent);
                saveToFile();
                return true;
            }
        }

        return false;
    }

    public boolean deleteById(String id) throws DataAccessException {
        Event event = findById(id);

        if (event == null) {
            return false;
        }

        events.remove(event);
        saveToFile();
        return true;
    }

    private void loadFromFile() throws DataAccessException {
        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }

        ArrayList<Event> loadedEvents = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                if (lineNumber == 1 && line.trim().equalsIgnoreCase(HEADER)) {
                    continue;
                }

                loadedEvents.add(parseEvent(line, lineNumber));
            }

            events.clear();
            events.addAll(loadedEvents);
        } catch (IOException ex) {
            throw new DataAccessException("Không thể đọc file " + filePath, ex);
        }
    }

    private void saveToFile() throws DataAccessException {
        File file = new File(filePath);
        File parentFolder = file.getParentFile();

        if (parentFolder != null && !parentFolder.exists() && !parentFolder.mkdirs()) {
            throw new DataAccessException("Không thể tạo thư mục " + parentFolder.getPath());
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();

            for (Event event : events) {
                writer.write(convertToCsv(event));
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new DataAccessException("Không thể ghi file " + filePath, ex);
        }
    }

    private String convertToCsv(Event event) {
        return safe(event.getId()) + "," + safe(event.getName()) + "," + safe(event.getEventDate()) + "," + safe(event.getLocation()) + "," + safe(event.getDescription()) + "," + event.getMaxParticipants() + "," + event.getStatus().name();
    }

    private Event parseEvent(String line, int lineNumber) throws DataAccessException {
        String[] parts = line.split(",", -1);

        if (parts.length != 7) {
            throw new DataAccessException("Dòng " + lineNumber + " trong events.csv không đủ 7 cột");
        }

        String id = parts[0].trim();
        String name = parts[1].trim();
        String eventDate = parts[2].trim();
        String location = parts[3].trim();
        String description = parts[4].trim();

        int maxParticipants;

        try {
            maxParticipants = Integer.parseInt(parts[5].trim());
        } catch (NumberFormatException ex) {
            throw new DataAccessException("Số lượng tối đa tại dòng " + lineNumber + " không hợp lệ", ex);
        }

        if (maxParticipants <= 0) {
            throw new DataAccessException("Số lượng tối đa tại dòng " + lineNumber + " phải lớn hơn 0");
        }

        EventStatus status;

        try {
            status = EventStatus.valueOf(parts[6].trim());
        } catch (IllegalArgumentException ex) {
            throw new DataAccessException("Trạng thái sự kiện tại dòng " + lineNumber + " không hợp lệ", ex);
        }

        return new Event(id, name, eventDate, location, description, maxParticipants, status);
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}