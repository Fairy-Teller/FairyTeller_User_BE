package jungle.fairyTeller.fairyTale.book.controller;

import jungle.fairyTeller.fairyTale.book.dto.BookDTO;
import jungle.fairyTeller.fairyTale.book.dto.ResponseDTO;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService service;

    @GetMapping("/mine")
    public ResponseEntity<?> getBooksByUserId(@AuthenticationPrincipal String userId) {
        List<BookEntity> books = service.retrieve(Integer.parseInt(userId));

        List<BookDTO> dtos = books.stream().map(BookDTO::new).collect(Collectors.toList());

        ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/my-newest")
    public ResponseEntity<?> getNewestBookByUserId(@AuthenticationPrincipal String userId) {
        BookEntity book = service.retrieveLatestByUserId(Integer.parseInt(userId));

        BookDTO dto = BookDTO.builder()
                .bookId(book.getBookId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .thumbnailUrl(book.getThumbnailUrl())
                .build();

        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    // 책을 만들면 해당 userId로 만든 모든 책을 반환한다
    public ResponseEntity<?> createBook(@AuthenticationPrincipal String userId, @RequestBody BookDTO dto) {
        try {
            BookEntity entity = BookDTO.toEntity(dto);
            entity.setAuthor(Integer.parseInt(userId));
            List<BookEntity> entities = service.create(entity);
            List<BookDTO> dtos = entities.stream().map(BookDTO::new).collect(Collectors.toList());
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().data(dtos).build();
            return ResponseEntity.ok().body(response);
        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}