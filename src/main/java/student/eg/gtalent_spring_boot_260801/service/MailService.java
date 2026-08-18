package student.eg.gtalent_spring_boot_260801.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    // 注入 JavaMailSender
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 發送電子郵件的方法
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            logger.error("Failed to send email. to={}, subject={}", to, subject, exception);
            throw exception;
        }
    }

    /**
     * 將書籍異動整理成一致的通知內容。
     * Controller 只要告訴我們「做了什麼」和「哪一本書」，不用重複組合信件文字。
     *
     * @param to 通知收件者（通常是管理者的 Gmail）
     * @param action 異動名稱，例如「新增」、「修改」或「刪除」
     * @param bookDescription 要顯示在信件中的書籍資訊
     */
    public void sendBookNotification(String to, String action, String bookDescription) {
        String subject = "書籍" + action + "通知";
        String text = "書籍已" + action + "。\n" + bookDescription;
        sendEmail(to, subject, text);
    }
}
