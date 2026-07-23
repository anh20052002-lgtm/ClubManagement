package clubmanagement.service;

import clubmanagement.exception.DataNotFoundException;
import clubmanagement.exception.DuplicateIdException;
import clubmanagement.exception.ValidationException;
import clubmanagement.model.Member;
import clubmanagement.repository.MemberRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import clubmanagement.exception.DataAccessException;
import clubmanagement.model.ExecutiveMember;
import clubmanagement.repository.RegistrationRepository;
import clubmanagement.util.DateUtils;

public class MemberService {
    private final MemberRepository repository;
    private final RegistrationRepository registrationRepository;

    public MemberService(MemberRepository repository, RegistrationRepository registrationRepository) {
        this.repository = repository;
        this.registrationRepository = registrationRepository;
    }

    public void addMember(Member member)
        throws ValidationException,
               DuplicateIdException,
               DataAccessException {

        validateMember(member);

        if (repository.findById(member.getId()) != null) {
            throw new DuplicateIdException(
                "Mã thành viên đã tồn tại"
            );
        }

        repository.add(member);
    }

    public ArrayList<Member> findAll() {
        return repository.findAll();
    }

    public Member findById(String id) throws ValidationException, DataNotFoundException {

        validateId(id);

        Member member = repository.findById(id);

        if (member == null) {
            throw new DataNotFoundException(
                    "Không tìm thấy thành viên có mã " + id
            );
        }

        return member;
    }

    public void updateMember(Member member) throws ValidationException, DataNotFoundException, DataAccessException {

        validateMember(member);

        if (repository.findById(member.getId()) == null) {
            throw new DataNotFoundException(
                "Không tìm thấy thành viên cần cập nhật"
            );
        }

        repository.update(member);
    }

    public void deleteMember(String id) throws ValidationException, DataNotFoundException, DataAccessException {

        validateId(id);
        
        if (registrationRepository.hasRegistrationByMemberId(id)) {
            throw new ValidationException("Không thể xóa thành viên vì đang có đăng ký sự kiện");
        }
        
        if (!repository.deleteById(id)) {
            throw new DataNotFoundException(
                "Không tìm thấy thành viên cần xóa"
            );
        }
    }

    public ArrayList<Member> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }

        ArrayList<Member> result = new ArrayList<>();
        String value = keyword.trim().toLowerCase();

        for (Member member : repository.findAll()) {
            if (containsIgnoreCase(member.getId(), value)
                    || containsIgnoreCase(
                            member.getFullName(), value)
                    || containsIgnoreCase(
                            member.getClassName(), value)
                    || containsIgnoreCase(
                            member.getDepartment(), value)) {
                result.add(member);
            }
        }

        return result;
    }

    private void validateMember(Member member)
            throws ValidationException {

        if (member == null) {
            throw new ValidationException(
                    "Thông tin thành viên không được null"
            );
        }

        validateId(member.getId());

        if (isBlank(member.getFullName())) {
            throw new ValidationException(
                    "Họ tên không được để trống"
            );
        }

        if (!isValidEmail(member.getEmail())) {
            throw new ValidationException(
                    "Email không đúng định dạng"
            );
        }

        if (!isValidPhone(member.getPhone())) {
            throw new ValidationException(
                    "Số điện thoại phải gồm đúng 10 chữ số"
            );
        }

        if (isBlank(member.getClassName())) {
            throw new ValidationException(
                    "Tên lớp không được để trống"
            );
        }

        if (isBlank(member.getDepartment())) {
            throw new ValidationException(
                    "Ban hoạt động không được để trống"
            );
        }
        
        if (containsComma(member.getFullName())
                || containsComma(member.getClassName())
                || containsComma(member.getDepartment())) {

            throw new ValidationException(
                "Họ tên, lớp và ban hoạt động "
                + "không được chứa dấu phẩy"
            );
        }
        
        if (member instanceof ExecutiveMember) {
            ExecutiveMember executiveMember
                    = (ExecutiveMember) member;

            if (isBlank(executiveMember.getPosition())) {
                throw new ValidationException(
                    "Chức vụ không được để trống"
                );
            }

            if (containsComma(executiveMember.getPosition())) {
                throw new ValidationException(
                    "Chức vụ không được chứa dấu phẩy"
                );
            }
        }   

        if (!DateUtils.isValidDate(member.getJoinDate())) {
            throw new ValidationException(
                    "Ngày tham gia phải có dạng dd/MM/yyyy"
            );
        }

        if (member.getStatus() == null) {
            throw new ValidationException(
                    "Trạng thái thành viên không được để trống"
            );
        }
    }

    private void validateId(String id)
            throws ValidationException {

        if (isBlank(id)) {
            throw new ValidationException(
                    "Mã thành viên không được để trống"
            );
        }

        if (!id.matches("TV\\d{3}")) {
            throw new ValidationException(
                    "Mã thành viên phải có dạng TV001"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        if (isBlank(email)) {
            return false;
        }

        return email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    private boolean isValidDate(String date) {
        if (isBlank(date)) {
            return false;
        }

        SimpleDateFormat format =
                new SimpleDateFormat("dd/MM/yyyy");

        format.setLenient(false);

        try {
            format.parse(date);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    private boolean containsIgnoreCase(
            String text, String keyword) {

        return text != null
                && text.toLowerCase().contains(keyword);
    }
    
    private boolean containsComma(String value) {
        return value != null && value.contains(",");
    }
}