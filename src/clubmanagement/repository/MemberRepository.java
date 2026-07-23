package clubmanagement.repository;

import clubmanagement.exception.DataAccessException;
import clubmanagement.model.ExecutiveMember;
import clubmanagement.model.Member;
import clubmanagement.model.MemberStatus;
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

public class MemberRepository {
    private static final String HEADER
            = "type,id,fullName,email,phone,className,"
            + "department,joinDate,status,position";

    private final ArrayList<Member> members;
    private final String filePath;

    public MemberRepository() throws DataAccessException {
        this("data/members.csv");
    }

    public MemberRepository(String filePath)
            throws DataAccessException {

        this.filePath = filePath;
        this.members = new ArrayList<>();

        loadFromFile();
    }

    public void add(Member member)
            throws DataAccessException {

        members.add(member);
        saveToFile();
    }

    public ArrayList<Member> findAll() {
        return new ArrayList<>(members);
    }

    public Member findById(String id) {
        if (id == null) {
            return null;
        }

        for (Member member : members) {
            if (member.getId().equalsIgnoreCase(id)) {
                return member;
            }
        }

        return null;
    }

    public boolean update(Member updatedMember)
            throws DataAccessException {

        if (updatedMember == null) {
            return false;
        }

        for (int i = 0; i < members.size(); i++) {
            Member currentMember = members.get(i);

            if (currentMember.getId().equalsIgnoreCase(
                    updatedMember.getId())) {

                members.set(i, updatedMember);
                saveToFile();

                return true;
            }
        }

        return false;
    }

    public boolean deleteById(String id)
            throws DataAccessException {

        Member member = findById(id);

        if (member == null) {
            return false;
        }

        members.remove(member);
        saveToFile();

        return true;
    }

    private void loadFromFile()
            throws DataAccessException {

        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }

        ArrayList<Member> loadedMembers = new ArrayList<>();

        try (
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8
                    )
            )
        ) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                if (lineNumber == 1
                        && line.trim().equalsIgnoreCase(HEADER)) {
                    continue;
                }

                Member member = parseMember(line, lineNumber);
                loadedMembers.add(member);
            }

            members.clear();
            members.addAll(loadedMembers);

        } catch (IOException ex) {
            throw new DataAccessException(
                    "Không thể đọc file " + filePath,
                    ex
            );
        }
    }

    private void saveToFile()
            throws DataAccessException {

        File file = new File(filePath);
        File parentFolder = file.getParentFile();

        if (parentFolder != null
                && !parentFolder.exists()
                && !parentFolder.mkdirs()) {

            throw new DataAccessException(
                    "Không thể tạo thư mục "
                    + parentFolder.getPath()
            );
        }

        try (
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file),
                            StandardCharsets.UTF_8
                    )
            )
        ) {
            writer.write(HEADER);
            writer.newLine();

            for (Member member : members) {
                writer.write(convertToCsv(member));
                writer.newLine();
            }

        } catch (IOException ex) {
            throw new DataAccessException(
                    "Không thể ghi file " + filePath,
                    ex
            );
        }
    }

    private String convertToCsv(Member member) {
        String type = "MEMBER";
        String position = "";

        if (member instanceof ExecutiveMember) {
            ExecutiveMember executiveMember
                    = (ExecutiveMember) member;

            type = "EXECUTIVE";
            position = executiveMember.getPosition();
        }

        return type + ","
                + safe(member.getId()) + ","
                + safe(member.getFullName()) + ","
                + safe(member.getEmail()) + ","
                + safe(member.getPhone()) + ","
                + safe(member.getClassName()) + ","
                + safe(member.getDepartment()) + ","
                + safe(member.getJoinDate()) + ","
                + member.getStatus().name() + ","
                + safe(position);
    }

    private Member parseMember(String line, int lineNumber)
            throws DataAccessException {

        String[] parts = line.split(",", -1);

        if (parts.length != 10) {
            throw new DataAccessException(
                    "Dòng " + lineNumber
                    + " trong file CSV không đủ 10 cột"
            );
        }

        String type = parts[0].trim();
        String id = parts[1].trim();
        String fullName = parts[2].trim();
        String email = parts[3].trim();
        String phone = parts[4].trim();
        String className = parts[5].trim();
        String department = parts[6].trim();
        String joinDate = parts[7].trim();
        String statusText = parts[8].trim();
        String position = parts[9].trim();

        MemberStatus status;

        try {
            status = MemberStatus.valueOf(statusText);
        } catch (IllegalArgumentException ex) {
            throw new DataAccessException(
                    "Trạng thái tại dòng "
                    + lineNumber + " không hợp lệ",
                    ex
            );
        }

        if ("MEMBER".equalsIgnoreCase(type)) {
            return new Member(
                    id,
                    fullName,
                    email,
                    phone,
                    className,
                    department,
                    joinDate,
                    status
            );
        }

        if ("EXECUTIVE".equalsIgnoreCase(type)) {
            return new ExecutiveMember(
                    id,
                    fullName,
                    email,
                    phone,
                    className,
                    department,
                    joinDate,
                    status,
                    position
            );
        }

        throw new DataAccessException(
                "Loại thành viên tại dòng "
                + lineNumber + " không hợp lệ"
        );
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}