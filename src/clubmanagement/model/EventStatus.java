package clubmanagement.model;

public enum EventStatus {
    UPCOMING("Sắp diễn ra"),
    ONGOING("Đang diễn ra"),
    COMPLETED("Đã kết thúc"),
    CANCELLED("Đã hủy");

    private final String displayName;

    EventStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}