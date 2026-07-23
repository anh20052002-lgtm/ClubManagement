package clubmanagement.repository;

import clubmanagement.exception.DataAccessException;
import clubmanagement.model.AttendanceStatus;
import clubmanagement.model.Registration;
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

public class RegistrationRepository {
    private static final String HEADER = "id,memberId,eventId,registrationDate,status";

    private final ArrayList<Registration> registrations;
    private final String filePath;

    public RegistrationRepository() throws DataAccessException {
        this("data/registrations.csv");
    }

    public RegistrationRepository(String filePath) throws DataAccessException {
        this.filePath = filePath;
        this.registrations = new ArrayList<>();
        loadFromFile();
    }

    public void add(Registration registration) throws DataAccessException {
        registrations.add(registration);
        saveToFile();
    }

    public ArrayList<Registration> findAll() {
        return new ArrayList<>(registrations);
    }

    public Registration findById(String id) {
        if (id == null) {
            return null;
        }

        for (Registration registration : registrations) {
            if (registration.getId().equalsIgnoreCase(id)) {
                return registration;
            }
        }

        return null;
    }

    public Registration findByMemberAndEvent(String memberId, String eventId) {
        for (Registration registration : registrations) {
            boolean sameMember = registration.getMemberId().equalsIgnoreCase(memberId);
            boolean sameEvent = registration.getEventId().equalsIgnoreCase(eventId);

            if (sameMember && sameEvent) {
                return registration;
            }
        }

        return null;
    }

    public boolean update(Registration updatedRegistration) throws DataAccessException {
        if (updatedRegistration == null) {
            return false;
        }

        for (int i = 0; i < registrations.size(); i++) {
            Registration currentRegistration = registrations.get(i);

            if (currentRegistration.getId().equalsIgnoreCase(updatedRegistration.getId())) {
                registrations.set(i, updatedRegistration);
                saveToFile();
                return true;
            }
        }

        return false;
    }

    public boolean deleteById(String id) throws DataAccessException {
        Registration registration = findById(id);

        if (registration == null) {
            return false;
        }

        registrations.remove(registration);
        saveToFile();
        return true;
    }

    public int countActiveRegistrationsByEventId(String eventId) {
        int count = 0;

        for (Registration registration : registrations) {
            boolean sameEvent = registration.getEventId().equalsIgnoreCase(eventId);
            boolean notCancelled = registration.getStatus() != AttendanceStatus.CANCELLED;

            if (sameEvent && notCancelled) {
                count++;
            }
        }

        return count;
    }

    public boolean hasRegistrationByMemberId(String memberId) {
        for (Registration registration : registrations) {
            if (registration.getMemberId().equalsIgnoreCase(memberId)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasRegistrationByEventId(String eventId) {
        for (Registration registration : registrations) {
            if (registration.getEventId().equalsIgnoreCase(eventId)) {
                return true;
            }
        }

        return false;
    }

    private void loadFromFile() throws DataAccessException {
        File file = new File(filePath);

        if (!file.exists()) {
            return;
        }

        ArrayList<Registration> loadedRegistrations = new ArrayList<>();

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

                loadedRegistrations.add(parseRegistration(line, lineNumber));
            }

            registrations.clear();
            registrations.addAll(loadedRegistrations);
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

            for (Registration registration : registrations) {
                writer.write(convertToCsv(registration));
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new DataAccessException("Không thể ghi file " + filePath, ex);
        }
    }

    private String convertToCsv(Registration registration) {
        return registration.getId() + "," + registration.getMemberId() + "," + registration.getEventId() + "," + registration.getRegistrationDate() + "," + registration.getStatus().name();
    }

    private Registration parseRegistration(String line, int lineNumber) throws DataAccessException {
        String[] parts = line.split(",", -1);

        if (parts.length != 5) {
            throw new DataAccessException("Dòng " + lineNumber + " trong registrations.csv không đủ 5 cột");
        }

        String id = parts[0].trim();
        String memberId = parts[1].trim();
        String eventId = parts[2].trim();
        String registrationDate = parts[3].trim();

        AttendanceStatus status;

        try {
            status = AttendanceStatus.valueOf(parts[4].trim());
        } catch (IllegalArgumentException ex) {
            throw new DataAccessException("Trạng thái đăng ký tại dòng " + lineNumber + " không hợp lệ", ex);
        }

        return new Registration(id, memberId, eventId, registrationDate, status);
    }
}