package clubmanagement.view;

import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;
import clubmanagement.service.RegistrationService;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
    private final MemberPanel memberPanel;
    private final EventPanel eventPanel;
    private final RegistrationPanel registrationPanel;

    public MainFrame(MemberService memberService, EventService eventService, RegistrationService registrationService) {
        memberPanel = new MemberPanel(memberService);
        eventPanel = new EventPanel(eventService);
        registrationPanel = new RegistrationPanel(registrationService);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Thành viên", memberPanel);
        tabbedPane.addTab("Sự kiện", eventPanel);
        tabbedPane.addTab("Đăng ký tham gia", registrationPanel);

        setTitle("Quản lý câu lạc bộ");
        setContentPane(tabbedPane);
        setSize(1350, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}