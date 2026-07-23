package clubmanagement.controller;

import clubmanagement.exception.ClubManagementException;
import clubmanagement.model.Event;
import clubmanagement.service.EventService;
import clubmanagement.view.EventFormDialog;
import clubmanagement.view.EventPanel;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.SwingUtilities;

public class EventController {
    private final EventService service;
    private final EventPanel view;

    public EventController(EventService service, EventPanel view) {
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
        EventFormDialog dialog = new EventFormDialog(getOwnerFrame(), this);
        dialog.setVisible(true);
    }

    public void openEditDialog() {
        String id = view.getSelectedEventId();

        if (id == null) {
            view.showError("Vui lòng chọn sự kiện cần sửa");
            return;
        }

        try {
            Event event = service.findById(id);
            EventFormDialog dialog = new EventFormDialog(getOwnerFrame(), this, event);
            dialog.setVisible(true);
        } catch (ClubManagementException ex) {
            view.showError(ex.getMessage());
        }
    }

    public void saveNewEvent(EventFormDialog dialog) {
        try {
            Event event = dialog.createEvent();

            service.addEvent(event);
            view.loadEvents();
            view.showMessage("Thêm sự kiện thành công");

            dialog.dispose();
        } catch (NumberFormatException ex) {
            dialog.showError("Số lượng tối đa phải là số nguyên");
        } catch (ClubManagementException ex) {
            dialog.showError(ex.getMessage());
        }
    }

    public void saveUpdatedEvent(EventFormDialog dialog) {
        try {
            Event event = dialog.createEvent();

            service.updateEvent(event);
            view.loadEvents();
            view.showMessage("Cập nhật sự kiện thành công");

            dialog.dispose();
        } catch (NumberFormatException ex) {
            dialog.showError("Số lượng tối đa phải là số nguyên");
        } catch (ClubManagementException ex) {
            dialog.showError(ex.getMessage());
        }
    }

    public void deleteSelectedEvent() {
        String id = view.getSelectedEventId();

        if (id == null) {
            view.showError("Vui lòng chọn sự kiện cần xóa");
            return;
        }

        if (!view.confirmDelete(id)) {
            return;
        }

        try {
            service.deleteEvent(id);
            view.loadEvents();
            view.showMessage("Xóa sự kiện thành công");
        } catch (ClubManagementException ex) {
            view.showError(ex.getMessage());
        }
    }

    public void searchEvents() {
        view.displayEvents(service.search(view.getSearchKeyword()));
    }

    public void refreshEvents() {
        view.clearSearchField();
        view.loadEvents();
    }
}