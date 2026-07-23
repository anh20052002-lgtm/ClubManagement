package clubmanagement.view;

import clubmanagement.controller.EventController;
import clubmanagement.model.Event;
import clubmanagement.service.EventService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class EventPanel extends JPanel {
    private final EventService service;
    private final EventController controller;

    private final DefaultTableModel tableModel;
    private final JTable eventTable;

    private final JTextField searchField;

    private final JButton searchButton;
    private final JButton refreshButton;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;

    public EventPanel(EventService service) {
        this.service = service;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("DANH SÁCH SỰ KIỆN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        searchField = new JTextField(20);

        searchButton = new JButton("Tìm kiếm");
        refreshButton = new JButton("Làm mới");
        addButton = new JButton("Thêm");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel toolPanel = new JPanel(new BorderLayout());
        toolPanel.add(searchPanel, BorderLayout.WEST);
        toolPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(toolPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Mã", "Tên sự kiện", "Ngày tổ chức", "Địa điểm", "Mô tả", "Số lượng tối đa", "Trạng thái"};

        tableModel = new DefaultTableModel(columns, 0);
        eventTable = new JTable(tableModel);

        eventTable.setDefaultEditor(Object.class, null);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        controller = new EventController(service, this);

        registerEvents();
        loadEvents();
    }

    private void registerEvents() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.openAddDialog();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.openEditDialog();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.deleteSelectedEvent();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.searchEvents();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.refreshEvents();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.searchEvents();
            }
        });
    }

    public void loadEvents() {
        displayEvents(service.findAll());
    }

    public void displayEvents(ArrayList<Event> events) {
        tableModel.setRowCount(0);

        for (Event event : events) {
            tableModel.addRow(new Object[]{event.getId(), event.getName(), event.getEventDate(), event.getLocation(), event.getDescription(), event.getMaxParticipants(), event.getStatus()});
        }

        eventTable.clearSelection();
    }

    public String getSelectedEventId() {
        int selectedRow = eventTable.getSelectedRow();

        if (selectedRow == -1) {
            return null;
        }

        int modelRow = eventTable.convertRowIndexToModel(selectedRow);

        return tableModel.getValueAt(modelRow, 0).toString();
    }

    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    public void clearSearchField() {
        searchField.setText("");
    }

    public boolean confirmDelete(String id) {
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sự kiện " + id + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        return choice == JOptionPane.YES_OPTION;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}