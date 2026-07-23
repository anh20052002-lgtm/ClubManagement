package clubmanagement.controller;

import clubmanagement.exception.ClubManagementException;
import clubmanagement.model.Member;
import clubmanagement.service.MemberService;
import clubmanagement.view.MemberFormDialog;
import clubmanagement.view.MemberPanel;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.SwingUtilities;

public class MemberController {
    private final MemberService service;
    private final MemberPanel view;

    public MemberController(MemberService service, MemberPanel view) {
        this.service = service;
        this.view = view;
    }

    private Frame getOwnerFrame() {
        Window window = SwingUtilities.getWindowAncestor(view);

        if (window instanceof Frame) {
            return (Frame) window;
        }

        return null;
    }

    public void openAddDialog() {
        MemberFormDialog dialog = new MemberFormDialog(getOwnerFrame(), this);
        dialog.setVisible(true);
    }

    public void openEditDialog() {
        String id = view.getSelectedMemberId();

        if (id == null) {
            view.showError("Vui lòng chọn thành viên cần sửa");
            return;
        }

        try {
            Member member = service.findById(id);
            MemberFormDialog dialog = new MemberFormDialog(getOwnerFrame(), this, member);
            dialog.setVisible(true);
        } catch (ClubManagementException ex) {
            view.showError(ex.getMessage());
        }
    }

    public void saveNewMember(MemberFormDialog dialog) {
        try {
            Member member = dialog.createMember();

            service.addMember(member);
            view.loadMembers();
            view.showMessage("Thêm thành viên thành công");

            dialog.dispose();
        } catch (ClubManagementException ex) {
            dialog.showError(ex.getMessage());
        }
    }

    public void saveUpdatedMember(MemberFormDialog dialog) {
        try {
            Member member = dialog.createMember();

            service.updateMember(member);
            view.loadMembers();
            view.showMessage("Cập nhật thành viên thành công");

            dialog.dispose();
        } catch (ClubManagementException ex) {
            dialog.showError(ex.getMessage());
        }
    }

    public void deleteSelectedMember() {
        String id = view.getSelectedMemberId();

        if (id == null) {
            view.showError("Vui lòng chọn thành viên cần xóa");
            return;
        }

        if (!view.confirmDelete(id)) {
            return;
        }

        try {
            service.deleteMember(id);
            view.loadMembers();
            view.showMessage("Xóa thành viên thành công");
        } catch (ClubManagementException ex) {
            view.showError(ex.getMessage());
        }
    }

    public void searchMembers() {
        String keyword = view.getSearchKeyword();
        view.displayMembers(service.search(keyword));
    }

    public void refreshMembers() {
        view.clearSearchField();
        view.loadMembers();
    }
}