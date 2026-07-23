package clubmanagement.model;

public class Event {
    private String id;
    private String name;
    private String eventDate;
    private String location;
    private String description;
    private int maxParticipants;
    private EventStatus status;

    public Event(String id, String name, String eventDate, String location, String description, int maxParticipants, EventStatus status) {
        this.id = id;
        this.name = name;
        this.eventDate = eventDate;
        this.location = location;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return id + " - " + name;
    }
}