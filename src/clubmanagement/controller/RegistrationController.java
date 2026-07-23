package clubmanagement.controller;

import clubmanagement.exception.ClubManagementException;
import clubmanagement.model.AttendanceStatus;
import clubmanagement.model.Event;
import clubmanagement.model.Member;
import clubmanagement.service.RegistrationService;
import clubmanagement.view.RegistrationPanel;

public class RegistrationController {
    private final RegistrationService service;
    private final RegistrationPanel view;

    public RegistrationController(RegistrationService service, RegistrationPanel view) {
        this.service = service;
        this.view = view;
    }

    public void registerMember() {
        Member member = view.getSelectedMember();
        Event event = view.getSelectedEvent();

        if (member == null) {
            view.showError("Vui lòng chọn thành viên");
            return;
        }

        if (event == null) {
            view.showError("Vui lòng chọn sự kiện");
            return;
        }

        try {
            service.registerMember(member.getId(), event.getId(), view.getRegistrationDate());
            view.loadRegistrations();
            view.showMessage("Đăng ký tham gia sự kiện thành công");
        } catch (ClubManagementException ex) {
            view.showError(ex.getMessage());
        }
    }

    public void updateSelectedStatus() {
        String registrationId = view.getSelectedRegistrationId();
        AttendanceStatus status = view.getSelectedAttendanceStatus();

        if (registrationId == null) {
            view.showError("Vui lòng chọn đăng ký cần cập nhật");
            return;
        }

        try {
            service.updateStatus(registrationId, status);
            view.loadRegistrations();
            view.showMessage("Cập nhật trạng thái thành công");
        } catch (ClubManagementException ex) {
            view.showError(ex.getMessage());
        }
    }

    public void deleteSelectedRegistration() {
        String registrationId = view.getSelectedRegistrationId();

        if (registrationId == null) {
            view.showError("Vui lòng chọn đăng ký cần xóa");
            return;
        }

        if (!view.confirmDelete(registrationId)) {
            return;
        }

        try {
            service.deleteRegistration(registrationId);
            view.loadRegistrations();
            view.showMessage("Xóa đăng ký thành công");
        } catch (ClubManagementException ex) {
            view.showError(ex.getMessage());
        }
    }

    public void searchRegistrations() {
        view.displayRegistrations(service.search(view.getSearchKeyword()));
    }

    public void refreshRegistrations() {
        view.clearSearchField();
        view.loadReferenceData();
        view.loadRegistrations();
    }
}