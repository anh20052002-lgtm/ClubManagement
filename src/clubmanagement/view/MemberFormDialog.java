package clubmanagement.view;

import clubmanagement.controller.MemberController;
import clubmanagement.model.ExecutiveMember;
import clubmanagement.model.Member;
import clubmanagement.model.MemberStatus;
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

public class MemberFormDialog extends JDialog {
    private final MemberController controller;
    private final boolean editMode;

    private final JTextField idField;
    private final JTextField fullNameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JTextField classNameField;
    private final JTextField departmentField;
    private final JTextField joinDateField;
    private final JTextField positionField;

    private final JComboBox<MemberStatus> statusComboBox;
    private final JComboBox<String> memberTypeComboBox;

    private final JButton saveButton;
    private final JButton cancelButton;

    public MemberFormDialog(Frame owner, MemberController controller) {
        this(owner, controller, null);
    }

    public MemberFormDialog(Frame owner, MemberController controller, Member member) {
        super(owner, member == null ? "Thêm thành viên" : "Sửa thành viên", true);

        this.controller = controller;
        this.editMode = member != null;

        idField = new JTextField(20);
        fullNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        classNameField = new JTextField(20);
        departmentField = new JTextField(20);
        joinDateField = new JTextField(20);
        positionField = new JTextField(20);

        statusComboBox = new JComboBox<>(MemberStatus.values());
        memberTypeComboBox = new JComboBox<>(new String[]{"Thành viên thường", "Ban chủ nhiệm"});

        saveButton = new JButton(editMode ? "Cập nhật" : "Lưu");
        cancelButton = new JButton("Hủy");

        initializeInterface();
        registerEvents();

        if (editMode) {
            loadMember(member);
        } else {
            updatePositionField();
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

        addFormRow(formPanel, gbc, 0, "Mã thành viên:", idField);
        addFormRow(formPanel, gbc, 1, "Họ tên:", fullNameField);
        addFormRow(formPanel, gbc, 2, "Email:", emailField);
        addFormRow(formPanel, gbc, 3, "Điện thoại:", phoneField);
        addFormRow(formPanel, gbc, 4, "Lớp:", classNameField);
        addFormRow(formPanel, gbc, 5, "Ban hoạt động:", departmentField);
        addFormRow(formPanel, gbc, 6, "Ngày tham gia:", joinDateField);
        addFormRow(formPanel, gbc, 7, "Trạng thái:", statusComboBox);
        addFormRow(formPanel, gbc, 8, "Loại thành viên:", memberTypeComboBox);
        addFormRow(formPanel, gbc, 9, "Chức vụ:", positionField);

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
        memberTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                updatePositionField();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (editMode) {
                    controller.saveUpdatedMember(MemberFormDialog.this);
                } else {
                    controller.saveNewMember(MemberFormDialog.this);
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

    private void loadMember(Member member) {
        idField.setText(member.getId());
        fullNameField.setText(member.getFullName());
        emailField.setText(member.getEmail());
        phoneField.setText(member.getPhone());
        classNameField.setText(member.getClassName());
        departmentField.setText(member.getDepartment());
        joinDateField.setText(member.getJoinDate());
        statusComboBox.setSelectedItem(member.getStatus());

        boolean isExecutive = member instanceof ExecutiveMember;

        memberTypeComboBox.setSelectedIndex(isExecutive ? 1 : 0);

        if (isExecutive) {
            ExecutiveMember executiveMember = (ExecutiveMember) member;
            positionField.setText(executiveMember.getPosition());
        }

        idField.setEditable(false);
        updatePositionField();
    }

    private void updatePositionField() {
        boolean isExecutive = memberTypeComboBox.getSelectedIndex() == 1;

        positionField.setEnabled(isExecutive);

        if (!isExecutive) {
            positionField.setText("");
        }
    }

    public Member createMember() {
        String id = idField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String className = classNameField.getText().trim();
        String department = departmentField.getText().trim();
        String joinDate = joinDateField.getText().trim();

        MemberStatus status = (MemberStatus) statusComboBox.getSelectedItem();
        boolean isExecutive = memberTypeComboBox.getSelectedIndex() == 1;

        if (isExecutive) {
            String position = positionField.getText().trim();

            return new ExecutiveMember(id, fullName, email, phone, className, department, joinDate, status, position);
        }

        return new Member(id, fullName, email, phone, className, department, joinDate, status);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
    }
}