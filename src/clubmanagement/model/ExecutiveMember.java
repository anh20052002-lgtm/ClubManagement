package clubmanagement.model;

public class ExecutiveMember extends Member {
    private String position;

    public ExecutiveMember(String id, String fullName, String email,
                           String phone, String className,
                           String department, String joinDate,
                           MemberStatus status, String position) {
        super(id, fullName, email, phone,
              className, department, joinDate, status);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String getRoleName() {
        return position;
    }
}