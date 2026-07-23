package clubmanagement.model;

public enum MemberStatus {
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngừng hoạt động");

    private final String displayName;

    MemberStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}