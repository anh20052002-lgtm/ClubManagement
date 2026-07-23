package clubmanagement.service;

import clubmanagement.exception.DataAccessException;
import clubmanagement.exception.DataNotFoundException;
import clubmanagement.exception.ValidationException;
import clubmanagement.model.AttendanceStatus;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import clubmanagement.model.Member;
import clubmanagement.model.MemberStatus;
import clubmanagement.model.Registration;
import clubmanagement.repository.EventRepository;
import clubmanagement.repository.MemberRepository;
import clubmanagement.repository.RegistrationRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import clubmanagement.util.DateUtils;

public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    public RegistrationService(RegistrationRepository registrationRepository, MemberRepository memberRepository, EventRepository eventRepository) {
        this.registrationRepository = registrationRepository;
        this.memberRepository = memberRepository;
        this.eventRepository = eventRepository;
    }

    public void registerMember(String memberId, String eventId, String registrationDate) throws ValidationException, DataNotFoundException, DataAccessException {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new ValidationException("Vui lòng chọn thành viên");
        }

        if (eventId == null || eventId.trim().isEmpty()) {
            throw new ValidationException("Vui lòng chọn sự kiện");
        }

        if (!DateUtils.isValidDate(registrationDate)) {
            throw new ValidationException("Ngày đăng ký phải có dạng dd/MM/yyyy");
        }

        Member member = memberRepository.findById(memberId);

        if (member == null) {
            throw new DataNotFoundException("Không tìm thấy thành viên có mã " + memberId);
        }

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new ValidationException("Chỉ thành viên đang hoạt động mới được đăng ký");
        }

        Event event = eventRepository.findById(eventId);

        if (event == null) {
            throw new DataNotFoundException("Không tìm thấy sự kiện có mã " + eventId);
        }

        if (event.getStatus() != EventStatus.UPCOMING) {
            throw new ValidationException("Chỉ được đăng ký sự kiện sắp diễn ra");
        }

        Registration existingRegistration = registrationRepository.findByMemberAndEvent(memberId, eventId);

        if (existingRegistration != null) {
            throw new ValidationException("Thành viên đã có đăng ký cho sự kiện này");
        }

        int currentParticipants = registrationRepository.countActiveRegistrationsByEventId(eventId);

        if (currentParticipants >= event.getMaxParticipants()) {
            throw new ValidationException("Sự kiện đã đủ số lượng người tham gia");
        }

        String registrationId = generateNextId();
        Registration registration = new Registration(registrationId, memberId, eventId, registrationDate, AttendanceStatus.REGISTERED);

        registrationRepository.add(registration);
    }

    public ArrayList<Registration> findAll() {
        return registrationRepository.findAll();
    }

    public Registration findById(String id) throws ValidationException, DataNotFoundException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Mã đăng ký không được để trống");
        }

        Registration registration = registrationRepository.findById(id);

        if (registration == null) {
            throw new DataNotFoundException("Không tìm thấy đăng ký có mã " + id);
        }

        return registration;
    }

    public void updateStatus(String registrationId, AttendanceStatus status) throws ValidationException, DataNotFoundException, DataAccessException {
        if (status == null) {
            throw new ValidationException("Trạng thái tham gia không được để trống");
        }

        Registration registration = findById(registrationId);

        if (registration.getStatus() == AttendanceStatus.CANCELLED && status == AttendanceStatus.REGISTERED) {
            Event event = eventRepository.findById(registration.getEventId());

            if (event == null) {
                throw new DataNotFoundException("Sự kiện của đăng ký không còn tồn tại");
            }

            if (event.getStatus() != EventStatus.UPCOMING) {
                throw new ValidationException("Không thể đăng ký lại sự kiện đã bắt đầu hoặc kết thúc");
            }

            int currentParticipants = registrationRepository.countActiveRegistrationsByEventId(event.getId());

            if (currentParticipants >= event.getMaxParticipants()) {
                throw new ValidationException("Sự kiện đã đủ số lượng người tham gia");
            }
        }

        registration.setStatus(status);
        registrationRepository.update(registration);
    }

    public void deleteRegistration(String id) throws ValidationException, DataNotFoundException, DataAccessException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Mã đăng ký không được để trống");
        }

        if (!registrationRepository.deleteById(id)) {
            throw new DataNotFoundException("Không tìm thấy đăng ký cần xóa");
        }
    }

    public ArrayList<Registration> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return registrationRepository.findAll();
        }

        String value = keyword.trim().toLowerCase();
        ArrayList<Registration> result = new ArrayList<>();

        for (Registration registration : registrationRepository.findAll()) {
            Member member = memberRepository.findById(registration.getMemberId());
            Event event = eventRepository.findById(registration.getEventId());

            String memberName = member == null ? "" : member.getFullName();
            String eventName = event == null ? "" : event.getName();

            if (containsIgnoreCase(registration.getId(), value) || containsIgnoreCase(registration.getMemberId(), value) || containsIgnoreCase(memberName, value) || containsIgnoreCase(registration.getEventId(), value) || containsIgnoreCase(eventName, value) || containsIgnoreCase(registration.getStatus().toString(), value)) {
                result.add(registration);
            }
        }

        return result;
    }

    public ArrayList<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public ArrayList<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    public String getMemberName(String memberId) {
        Member member = memberRepository.findById(memberId);

        if (member == null) {
            return "Không tồn tại";
        }

        return member.getFullName();
    }

    public String getEventName(String eventId) {
        Event event = eventRepository.findById(eventId);

        if (event == null) {
            return "Không tồn tại";
        }

        return event.getName();
    }

    private String generateNextId() {
        int maximumNumber = 0;

        for (Registration registration : registrationRepository.findAll()) {
            String id = registration.getId();

            if (id != null && id.matches("DK\\d{3}")) {
                int number = Integer.parseInt(id.substring(2));

                if (number > maximumNumber) {
                    maximumNumber = number;
                }
            }
        }

        return String.format("DK%03d", maximumNumber + 1);
    }

    private boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
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
}