package clubmanagement.model;

public class Member extends Person {
    private String className;
    private String department;
    private String joinDate;
    private MemberStatus status;

    public Member(String id, String fullName, String email, String phone,
                  String className, String department, String joinDate,
                  MemberStatus status) {
        super(id, fullName, email, phone);
        this.className = className;
        this.department = department;
        this.joinDate = joinDate;
        this.status = status;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    @Override
    public String getRoleName() {
        return "Thành viên";
    }
    @Override
    public String toString() {
        return getId() + " - " + getFullName();
    }
}