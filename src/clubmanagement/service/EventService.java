package clubmanagement.service;

import clubmanagement.exception.DataAccessException;
import clubmanagement.exception.DataNotFoundException;
import clubmanagement.exception.DuplicateIdException;
import clubmanagement.exception.ValidationException;
import clubmanagement.model.Event;
import clubmanagement.repository.EventRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import clubmanagement.repository.RegistrationRepository;
import clubmanagement.util.DateUtils;

public class EventService {
    private final EventRepository repository;
    private final RegistrationRepository registrationRepository;

    public EventService(EventRepository repository, RegistrationRepository registrationRepository) {
        this.repository = repository;
        this.registrationRepository = registrationRepository;
    }

    public void addEvent(Event event) throws ValidationException, DuplicateIdException, DataAccessException {
        validateEvent(event);

        if (repository.findById(event.getId()) != null) {
            throw new DuplicateIdException("Mã sự kiện đã tồn tại");
        }

        repository.add(event);
    }

    public ArrayList<Event> findAll() {
        return repository.findAll();
    }

    public Event findById(String id) throws ValidationException, DataNotFoundException {
        validateId(id);

        Event event = repository.findById(id);

        if (event == null) {
            throw new DataNotFoundException("Không tìm thấy sự kiện có mã " + id);
        }

        return event;
    }

    public void updateEvent(Event event) throws ValidationException, DataNotFoundException, DataAccessException {
        validateEvent(event);

        if (repository.findById(event.getId()) == null) {
            throw new DataNotFoundException("Không tìm thấy sự kiện cần cập nhật");
        }

        repository.update(event);
    }

    public void deleteEvent(String id) throws ValidationException, DataNotFoundException, DataAccessException {
        validateId(id);
        
        if (registrationRepository.hasRegistrationByEventId(id)) {
            throw new ValidationException("Không thể xóa sự kiện vì đang có thành viên đăng ký");
        }
        
        if (!repository.deleteById(id)) {
            throw new DataNotFoundException("Không tìm thấy sự kiện cần xóa");
        }
    }

    public ArrayList<Event> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }

        ArrayList<Event> result = new ArrayList<>();
        String value = keyword.trim().toLowerCase();

        for (Event event : repository.findAll()) {
            if (containsIgnoreCase(event.getId(), value) || containsIgnoreCase(event.getName(), value) || containsIgnoreCase(event.getLocation(), value) || containsIgnoreCase(event.getStatus().toString(), value)) {
                result.add(event);
            }
        }

        return result;
    }

    private void validateEvent(Event event) throws ValidationException {
        if (event == null) {
            throw new ValidationException("Thông tin sự kiện không được null");
        }

        validateId(event.getId());

        if (isBlank(event.getName())) {
            throw new ValidationException("Tên sự kiện không được để trống");
        }

        if (!DateUtils.isValidDate(event.getEventDate())) {
            throw new ValidationException("Ngày tổ chức phải có dạng dd/MM/yyyy");
        }

        if (isBlank(event.getLocation())) {
            throw new ValidationException("Địa điểm không được để trống");
        }

        if (event.getMaxParticipants() <= 0) {
            throw new ValidationException("Số lượng tối đa phải lớn hơn 0");
        }

        if (event.getStatus() == null) {
            throw new ValidationException("Trạng thái sự kiện không được để trống");
        }

        if (containsComma(event.getName()) || containsComma(event.getLocation()) || containsComma(event.getDescription())) {
            throw new ValidationException("Tên, địa điểm và mô tả không được chứa dấu phẩy");
        }
    }

    private void validateId(String id) throws ValidationException {
        if (isBlank(id)) {
            throw new ValidationException("Mã sự kiện không được để trống");
        }

        if (!id.matches("SK\\d{3}")) {
            throw new ValidationException("Mã sự kiện phải có dạng SK001");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidDate(String date) {
        if (isBlank(date)) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);

        try {
            format.parse(date);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword);
    }

    private boolean containsComma(String value) {
        return value != null && value.contains(",");
    }
}