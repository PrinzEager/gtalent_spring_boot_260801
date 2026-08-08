package student.eg.gtalent_spring_boot_260801.repository;

import student.eg.gtalent_spring_boot_260801.entity.Book;

import java.util.List;

public interface BookRepository {

    // 取得所有書籍
    List<Book> findAll();
    
    // 新增一本書籍
    Book create(Book book);

    // 修改一本書籍
    public Book update(Long id,Book book);

}