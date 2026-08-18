package student.eg.gtalent_spring_boot_260801.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import student.eg.gtalent_spring_boot_260801.entity.Book;
import student.eg.gtalent_spring_boot_260801.repository.BookRepository;
import student.eg.gtalent_spring_boot_260801.request.BookCreateRequest;
import student.eg.gtalent_spring_boot_260801.response.ApiResponse;
import student.eg.gtalent_spring_boot_260801.service.MailService;

import java.util.List;

/** 處理書籍 CRUD（新增、查詢、修改、刪除）的 API。 */
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository repository;
    private final MailService mailService;
    private final String notificationMailAddress;

    /**
     * 建構子注入需要的元件。
     * {@code book.notification.to} 寫在 application.properties；預設空字串讓測試環境
     * 沒有設定 Gmail 時仍可正常啟動。
     */
    public BookController(
            BookRepository repository,
            MailService mailService,
            @Value("${book.notification.to:}") String notificationMailAddress) {
        this.repository = repository;
        this.mailService = mailService;
        this.notificationMailAddress = notificationMailAddress;
    }

    /** 取得所有尚未刪除的書籍。 */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll() {
        return repository.findAll();
    }

    /** 依書籍 ID 取得一筆書籍。 */
    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Book getOneById(@PathVariable Long id) {
        return repository.findOneById(id);
    }

    /** 依書名關鍵字搜尋書籍。 */
    @GetMapping("/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getOneByName(@PathVariable String name) {
        return repository.findOneByName(name);
    }

    /**
     * 新增一本書，資料庫成功寫入後才寄出通知。
     * {@code @Valid} 會執行 BookCreateRequest 裡的欄位驗證。
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(@Valid @RequestBody BookCreateRequest request) {
        Book book = new Book(request.getName(), request.getPrice());
        repository.create(book);
        sendBookNotification("新增", formatBook(book));
        return new ApiResponse("新增書籍成功");
    }

    /** 修改指定 ID 的書籍，成功後寄出通知。 */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(@PathVariable Long id, @Valid @RequestBody BookCreateRequest request) {
        Book book = new Book(request.getName(), request.getPrice());
        repository.update(id, book);
        sendBookNotification("修改", "書籍 ID：" + id + "\n" + formatBook(book));
        return new ApiResponse("修改書籍成功");
    }

    /**
     * 軟刪除指定書籍，成功後寄出通知。
     * 先查詢可保留書名和價格供通知信使用；查不到資料時會拋例外，所以不會誤寄信件。
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(@PathVariable Long id) {
        Book book = repository.findOneById(id);
        repository.delete(id);
        sendBookNotification("刪除", "書籍 ID：" + id + "\n" + formatBook(book));
        return new ApiResponse("刪除書籍成功");
    }

    /** 將書籍欄位轉成容易閱讀的信件內容。 */
    private String formatBook(Book book) {
        return "書名：" + book.getName() + "\n價格：" + book.getPrice();
    }

    /**
     * 沒有設定收件人時只略過寄信，讓尚未設定 Gmail 的初學者仍可練習 CRUD。
     * 一旦設定 book.notification.to，每次新增、修改、刪除成功後就會呼叫 MailService 寄信。
     */
    private void sendBookNotification(String action, String bookDescription) {
        if (notificationMailAddress == null || notificationMailAddress.isBlank()) {
            return;
        }
        mailService.sendBookNotification(notificationMailAddress, action, bookDescription);
    }
}
