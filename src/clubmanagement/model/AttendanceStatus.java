package clubmanagement.model;

public enum AttendanceStatus {
    REGISTERED("Đã đăng ký"),
    ATTENDED("Đã tham gia"),
    ABSENT("Vắng mặt"),
    CANCELLED("Đã hủy");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}