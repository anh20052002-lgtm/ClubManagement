package clubmanagement.view;

import clubmanagement.controller.MemberController;
import clubmanagement.model.Member;
import clubmanagement.service.MemberService;
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

public class MemberPanel extends JPanel {
    private final MemberService service;
    private final MemberController controller;

    private final DefaultTableModel tableModel;
    private final JTable memberTable;

    private final JTextField searchField;

    private final JButton searchButton;
    private final JButton refreshButton;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;

    public MemberPanel(MemberService service) {
        this.service = service;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("DANH SÁCH THÀNH VIÊN");
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

        String[] columns = {"Mã", "Họ tên", "Email", "Điện thoại", "Lớp", "Ban", "Ngày tham gia", "Trạng thái", "Vai trò"};

        tableModel = new DefaultTableModel(columns, 0);
        memberTable = new JTable(tableModel);

        memberTable.setDefaultEditor(Object.class, null);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(memberTable);

        add(scrollPane, BorderLayout.CENTER);

        controller = new MemberController(service, this);

        registerEvents();
        loadMembers();
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
                controller.deleteSelectedMember();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.searchMembers();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.refreshMembers();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                controller.searchMembers();
            }
        });
    }

    public void loadMembers() {
        displayMembers(service.findAll());
    }

    public void displayMembers(ArrayList<Member> members) {
        tableModel.setRowCount(0);

        for (Member member : members) {
            tableModel.addRow(new Object[]{member.getId(), member.getFullName(), member.getEmail(), member.getPhone(), member.getClassName(), member.getDepartment(), member.getJoinDate(), member.getStatus(), member.getRoleName()});
        }

        memberTable.clearSelection();
    }

    public String getSelectedMemberId() {
        int selectedRow = memberTable.getSelectedRow();

        if (selectedRow == -1) {
            return null;
        }

        int modelRow = memberTable.convertRowIndexToModel(selectedRow);

        return tableModel.getValueAt(modelRow, 0).toString();
    }

    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    public void clearSearchField() {
        searchField.setText("");
    }

    public boolean confirmDelete(String id) {
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa thành viên " + id + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        return choice == JOptionPane.YES_OPTION;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}