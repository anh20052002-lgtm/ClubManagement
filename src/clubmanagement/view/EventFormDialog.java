package clubmanagement.view;

import clubmanagement.controller.EventController;
import clubmanagement.model.Event;
import clubmanagement.model.EventStatus;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EventFormDialog extends JDialog {
    private final EventController controller;
    private final boolean editMode;

    private final JTextField idField;
    private final JTextField nameField;
    private final JTextField dateField;
    private final JTextField locationField;
    private final JTextField descriptionField;
    private final JTextField maxParticipantsField;

    private final JComboBox<EventStatus> statusComboBox;

    private final JButton saveButton;
    private final JButton cancelButton;

    public EventFormDialog(Frame owner, EventController controller) {
        this(owner, controller, null);
    }

    public EventFormDialog(Frame owner, EventController controller, Event event) {
        super(owner, event == null ? "Thêm sự kiện" : "Sửa sự kiện", true);

        this.controller = controller;
        this.editMode = event != null;

        idField = new JTextField(20);
        nameField = new JTextField(20);
        dateField = new JTextField(20);
        locationField = new JTextField(20);
        descriptionField = new JTextField(20);
        maxParticipantsField = new JTextField(20);

        statusComboBox = new JComboBox<>(EventStatus.values());

        saveButton = new JButton(editMode ? "Cập nhật" : "Lưu");
        cancelButton = new JButton("Hủy");

        initializeInterface();
        registerEvents();

        if (editMode) {
            loadEvent(event);
        }

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initializeInterface() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formPanel, gbc, 0, "Mã sự kiện:", idField);
        addFormRow(formPanel, gbc, 1, "Tên sự kiện:", nameField);
        addFormRow(formPanel, gbc, 2, "Ngày tổ chức:", dateField);
        addFormRow(formPanel, gbc, 3, "Địa điểm:", locationField);
        addFormRow(formPanel, gbc, 4, "Mô tả:", descriptionField);
        addFormRow(formPanel, gbc, 5, "Số lượng tối đa:", maxParticipantsField);
        addFormRow(formPanel, gbc, 6, "Trạng thái:", statusComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveButton);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(component, gbc);
    }

    private void registerEvents() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (editMode) {
                    controller.saveUpdatedEvent(EventFormDialog.this);
                } else {
                    controller.saveNewEvent(EventFormDialog.this);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
    }

    private void loadEvent(Event event) {
        idField.setText(event.getId());
        nameField.setText(event.getName());
        dateField.setText(event.getEventDate());
        locationField.setText(event.getLocation());
        descriptionField.setText(event.getDescription());
        maxParticipantsField.setText(String.valueOf(event.getMaxParticipants()));
        statusComboBox.setSelectedItem(event.getStatus());

        idField.setEditable(false);
    }

    public Event createEvent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String date = dateField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionField.getText().trim();
        int maxParticipants = Integer.parseInt(maxParticipantsField.getText().trim());
        EventStatus status = (EventStatus) statusComboBox.getSelectedItem();

        return new Event(id, name, date, location, description, maxParticipants, status);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
    }
}