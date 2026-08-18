package student.eg.gtalent_spring_boot_260801.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import student.eg.gtalent_spring_boot_260801.request.MailSendRequest;
import student.eg.gtalent_spring_boot_260801.response.ApiResponse;
import student.eg.gtalent_spring_boot_260801.service.MailService;

/**
 * 提供寄送電子郵件的 API。
 */
@RestController
public class MailController {

    private final MailService mailService;

    // 透過建構子注入實際負責寄信的 Service。
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * 使用 JSON Request Body 寄送 Gmail。
     *
     * <p>呼叫時請設定 {@code Content-Type: application/json}。</p>
     * <pre>
     * POST /send/gmail
     * {
     *   "toMailAddress": "student@example.com",
     *   "subject": "測試信件",
     *   "content": "您好，這是一封測試信件。"
     * }
     * </pre>
     *
     * @param request JSON 轉換後的寄信資料；{@code @Valid} 會執行其欄位驗證
     */
    @PostMapping("/send/gmail")
    public ApiResponse sendEmail(@Valid @RequestBody MailSendRequest request) {
        // @RequestBody 將 JSON 轉成 DTO，@Valid 驗證 email、主旨及內容是否合法。
        mailService.sendEmail(
                request.getToMailAddress(),
                request.getSubject(),
                request.getContent());

        return new ApiResponse("Gmail 寄送成功");
    }
}
