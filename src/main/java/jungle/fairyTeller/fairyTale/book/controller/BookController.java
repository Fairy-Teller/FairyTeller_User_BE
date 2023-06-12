package jungle.fairyTeller.fairyTale.book.controller;

import jungle.fairyTeller.fairyTale.Image.service.SaveImgService;
import jungle.fairyTeller.fairyTale.audio.service.TtsService;
import jungle.fairyTeller.fairyTale.book.dto.BookDTO;
import jungle.fairyTeller.fairyTale.book.dto.ResponseDTO;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.service.BookService;
import jungle.fairyTeller.fairyTale.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private SaveImgService saveImgService;

    @Autowired
    private TtsService ttsService;

    @Autowired
    private FileService fileService;

    @GetMapping("/getBookById")
    public ResponseEntity<?> getBookByBookId(@RequestBody BookDTO bookDTO,@AuthenticationPrincipal String userId){
        BookEntity book = bookService.getBookByBookId(bookDTO.getBookId());

        BookDTO dto = BookDTO.builder()
                .bookId(book.getBookId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .fullStory(book.getFullStory())
                .thumbnailUrl(book.getThumbnailUrl())
                .audioUrl(book.getAudioUrl())
                .build();

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getBooksByUserId(@AuthenticationPrincipal String userId) {
        List<BookEntity> books = bookService.retrieve(Integer.parseInt(userId));

        List<BookDTO> dtos = books.stream().map(BookDTO::new).collect(Collectors.toList());

        ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().data(dtos).build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/my-newest")
    public ResponseEntity<?> getNewestBookByUserId(@AuthenticationPrincipal String userId) {
        BookEntity book = bookService.retrieveLatestByUserId(Integer.parseInt(userId));

        BookDTO dto = BookDTO.builder()
                .bookId(book.getBookId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .thumbnailUrl(book.getThumbnailUrl())
                .build();

        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/create/story")
    // 사용자가 줄거리를 확정하면, bookID를 생성하고 확정된 줄거리를 저장한다
    public ResponseEntity<?> createStory(@AuthenticationPrincipal String userId, @RequestBody BookDTO dto) {
        try {
            BookEntity entity = BookDTO.toEntity(dto);
            entity.setAuthor(Integer.parseInt(userId));

            BookEntity savedBook = bookService.createBookIdAndSaveStory(entity);

            BookDTO savedBookDto = BookDTO.builder()
                    .bookId(savedBook.getBookId())
                    .author(savedBook.getAuthor())
                    .title("임시"+savedBook.getBookId())
                    .fullStory(savedBook.getFullStory())
                    .build();

            return ResponseEntity.ok().body(savedBookDto);
        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/create/final")
    // 최종 책 제목, 이미지, 오디오 파일을 저장한다
    public ResponseEntity<?> saveFinalImgAndAudio(@AuthenticationPrincipal String userId, @RequestBody BookDTO dto) {
        try {
            // 기존에 저장된 bookEntity 찾기
            BookEntity originalBook = bookService.retrieveByBookId(dto.getBookId());

            // 0. 최종 제목
            originalBook.setTitle(dto.getTitle());

            // 1. 이미지
            try {
                String fileName = String.valueOf(originalBook.getBookId());
                // 1-1. 이미지를 바이트 배열로 변환
                byte[] imageContent = saveImgService.convertBase64ToImage(dto.getThumbnailUrl());
                // 1-2. 이미지를 저장경로에 저장한다.
                String imgUrl = fileService.uploadFile(imageContent, fileName + ".png");
                // 1-3. imgUrl 변수에 경로를 담는다
                originalBook.setThumbnailUrl(imgUrl);

            } catch (Exception e) {
                throw new RuntimeException("Error converting image: " + e.getMessage(), e);
            }

            // 2. tts
            try {
                String fileName = String.valueOf(originalBook.getBookId());
                // 2-1. tts를 호출한다
                byte[] audioContent = ttsService.synthesizeText(originalBook.getFullStory(), fileName);
                // 2-2. 생성된 오디오파일을 저장경로에 저장한다.
                String audioUrl = fileService.uploadFile(audioContent, fileName + ".mp3");
                // 2-3. audioUrl 변수에 경로를 담는다.
                originalBook.setAudioUrl(audioUrl);

            } catch (Exception e) {
                throw new RuntimeException("Error synthesizing text: " + e.getMessage(), e);
            }

            // 3. bookEntity를 db에 저장한다
            bookService.updateTitleStoryAudio(originalBook);

            BookDTO savedBookDto = BookDTO.builder()
                    .bookId(originalBook.getBookId())
                    .author(originalBook.getAuthor())
                    .title(originalBook.getTitle())
                    .fullStory(originalBook.getFullStory())
                    .thumbnailUrl(originalBook.getThumbnailUrl())
                    .audioUrl(originalBook.getAudioUrl())
                    .build();

            return ResponseEntity.ok().body(savedBookDto);
        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }


}
