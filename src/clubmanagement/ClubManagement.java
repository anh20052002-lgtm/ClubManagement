package clubmanagement;

import clubmanagement.exception.DataAccessException;
import clubmanagement.repository.EventRepository;
import clubmanagement.repository.MemberRepository;
import clubmanagement.repository.RegistrationRepository;
import clubmanagement.service.EventService;
import clubmanagement.service.MemberService;
import clubmanagement.service.RegistrationService;
import clubmanagement.view.MainFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ClubManagement {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startApplication();
            }
        });
    }

    private static void startApplication() {
        try {
            MemberRepository memberRepository = new MemberRepository();
            EventRepository eventRepository = new EventRepository();
            RegistrationRepository registrationRepository = new RegistrationRepository();

            MemberService memberService = new MemberService(memberRepository, registrationRepository);
            EventService eventService = new EventService(eventRepository, registrationRepository);
            RegistrationService registrationService = new RegistrationService(registrationRepository, memberRepository, eventRepository);

            MainFrame mainFrame = new MainFrame(memberService, eventService, registrationService);
            mainFrame.setVisible(true);
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Lỗi đọc dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
}