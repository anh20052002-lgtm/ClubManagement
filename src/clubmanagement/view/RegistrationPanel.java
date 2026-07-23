package clubmanagement.view;

import clubmanagement.controller.RegistrationController;
import clubmanagement.model.AttendanceStatus;
import clubmanagement.model.Event;
import clubmanagement.model.Member;
import clubmanagement.model.Registration;
import clubmanagement.service.RegistrationService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class RegistrationPanel extends JPanel {
    private final RegistrationService service;
    private final RegistrationController controller;

    private final JComboBox<Member> memberComboBox;
    private final JComboBox<Event> eventComboBox;
    private final JTextField registrationDateField;

    private final JTextField searchField;
    private final JComboBox<AttendanceStatus> attendanceStatusComboBox;

    private final JButton registerButton;
    private final JButton searchButton;
    private final JButton refreshButton;
    private final JButton updateStatusButton;
    private final JButton deleteButton;

    private final DefaultTableModel tableModel;
    private final JTable registrationTable;

    public RegistrationPanel(RegistrationService service) {
        this.service = service;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("ĐĂNG KÝ THAM GIA SỰ KIỆN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        memberComboBox = new JComboBox<>();
        eventComboBox = new JComboBox<>();
        registrationDateField = new JTextField(10);
        registrationDateField.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        registerButton = new JButton("Đăng ký");

        JPanel registrationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        registrationPanel.add(new JLabel("Thành viên:"));
        registrationPanel.add(memberComboBox);
        registrationPanel.add(new JLabel("Sự kiện:"));
        registrationPanel.add(eventComboBox);
        registrationPanel.add(new JLabel("Ngày đăng ký:"));
        registrationPanel.add(registrationDateField);
        registrationPanel.add(registerButton);

        searchField = new JTextField(18);
        searchButton = new JButton("Tìm kiếm");
        refreshButton = new JButton("Làm mới");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        attendanceStatusComboBox = new JComboBox<>(AttendanceStatus.values());
        updateStatusButton = new JButton("Cập nhật trạng thái");
        deleteButton = new JButton("Xóa đăng ký");

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(new JLabel("Trạng thái:"));
        actionPanel.add(attendanceStatusComboBox);
        actionPanel.add(updateStatusButton);
        actionPanel.add(deleteButton);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(searchPanel, BorderLayout.WEST);
        controlPanel.add(actionPanel, BorderLayout.EAST);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(registrationPanel, BorderLayout.CENTER);
        headerPanel.add(controlPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Mã đăng ký", "Mã thành viên", "Tên thành viên", "Mã sự kiện", "Tên sự kiện", "Ngày đăng ký", "Trạng thái"};

        tableModel = new DefaultTableModel(columns, 0);
        registrationTable = new JTable(tableModel);

        registrationTable.setDefaultEditor(Object.class, null);
        registrationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(registrationTable), BorderLayout.CENTER);

        controller = new RegistrationController(service, this);

        registerEvents();
        loadReferenceData();
        loadRegistrations();
    }

    private void registerEvents() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.registerMember();
            }
        });

        updateStatusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.updateSelectedStatus();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.deleteSelectedRegistration();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.searchRegistrations();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.refreshRegistrations();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.searchRegistrations();
            }
        });
    }

    public void loadReferenceData() {
        memberComboBox.removeAllItems();
        eventComboBox.removeAllItems();

        for (Member member : service.findAllMembers()) {
            memberComboBox.addItem(member);
        }

        for (Event event : service.findAllEvents()) {
            eventComboBox.addItem(event);
        }
    }

    public void loadRegistrations() {
        displayRegistrations(service.findAll());
    }

    public void displayRegistrations(ArrayList<Registration> registrations) {
        tableModel.setRowCount(0);

        for (Registration registration : registrations) {
            String memberName = service.getMemberName(registration.getMemberId());
            String eventName = service.getEventName(registration.getEventId());

            tableModel.addRow(new Object[]{registration.getId(), registration.getMemberId(), memberName, registration.getEventId(), eventName, registration.getRegistrationDate(), registration.getStatus()});
        }

        registrationTable.clearSelection();
    }

    public Member getSelectedMember() {
        return (Member) memberComboBox.getSelectedItem();
    }

    public Event getSelectedEvent() {
        return (Event) eventComboBox.getSelectedItem();
    }

    public String getRegistrationDate() {
        return registrationDateField.getText().trim();
    }

    public String getSelectedRegistrationId() {
        int selectedRow = registrationTable.getSelectedRow();

        if (selectedRow == -1) {
            return null;
        }

        int modelRow = registrationTable.convertRowIndexToModel(selectedRow);

        return tableModel.getValueAt(modelRow, 0).toString();
    }

    public AttendanceStatus getSelectedAttendanceStatus() {
        return (AttendanceStatus) attendanceStatusComboBox.getSelectedItem();
    }

    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    public void clearSearchField() {
        searchField.setText("");
    }

    public boolean confirmDelete(String registrationId) {
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa đăng ký " + registrationId + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        return choice == JOptionPane.YES_OPTION;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}