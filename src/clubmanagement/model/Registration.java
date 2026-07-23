package clubmanagement.model;

public class Registration {
    private String id;
    private String memberId;
    private String eventId;
    private String registrationDate;
    private AttendanceStatus status;

    public Registration(String id, String memberId, String eventId, String registrationDate, AttendanceStatus status) {
        this.id = id;
        this.memberId = memberId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}