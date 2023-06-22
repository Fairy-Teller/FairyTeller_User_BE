package jungle.fairyTeller.fairyTale.book.controller;

import jungle.fairyTeller.fairyTale.Image.service.SaveImgService;
import jungle.fairyTeller.fairyTale.Image.service.ThumbnailService;
import jungle.fairyTeller.fairyTale.audio.service.TtsService;
import jungle.fairyTeller.fairyTale.book.dto.BookDTO;
import jungle.fairyTeller.fairyTale.book.dto.PageDTO;
import jungle.fairyTeller.fairyTale.book.dto.ResponseDTO;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageId;
import jungle.fairyTeller.fairyTale.book.service.BookService;
import jungle.fairyTeller.fairyTale.book.service.PageService;
import jungle.fairyTeller.fairyTale.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private PageService pageService;
    @Autowired
    private SaveImgService saveImgService;
    @Autowired
    private TtsService ttsService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ThumbnailService thumbnailService;

    @PostMapping("/getBookById")
    public ResponseEntity<?> getBookByBookId(@RequestBody BookDTO bookDTO,@AuthenticationPrincipal String userId){
        BookEntity bookEntity = bookService.getBookByBookId(bookDTO.getBookId());

        List<PageDTO> pageDtos = getPageDTOS(bookEntity);

        BookDTO dto = BookDTO.builder()
                .bookId(bookEntity.getBookId())
                .author(bookEntity.getAuthor())
                .title(bookEntity.getTitle())
                .pages(pageDtos)
                .build();

        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ResponseDTO<BookDTO>> getBookById(@PathVariable Integer bookId,
                                                            @AuthenticationPrincipal String userId)
    {
        Optional<BookEntity> bookOptional = Optional.ofNullable(bookService.getBookByBookId(bookId));
        if (bookOptional.isPresent()) {
            BookEntity bookEntity = bookOptional.get();

            List<PageDTO> pageDtos = getPageDTOS(bookEntity);

            BookDTO bookDto = BookDTO.builder()
                    .bookId(bookEntity.getBookId())
                    .author(bookEntity.getAuthor())
                    .title(bookEntity.getTitle())
                    .pages(pageDtos)
                    .build();

            ResponseDTO<BookDTO> response = new ResponseDTO<>();
            response.setData(Collections.singletonList(bookDto));

            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
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
        BookEntity bookEntity = bookService.retrieveLatestByUserId(Integer.parseInt(userId));

        List<PageDTO> pageDtos = getPageDTOS(bookEntity);

        BookDTO bookDto = BookDTO.builder()
                .bookId(bookEntity.getBookId())
                .author(bookEntity.getAuthor())
                .title(bookEntity.getTitle())
                .pages(pageDtos)
                .build();

        return ResponseEntity.ok().body(bookDto);
    }

    @PostMapping("/create/story")
    // 사용자가 줄거리를 확정하면, bookID를 생성하고 확정된 줄거리를 저장한다
    // TTS도 이 때 생성된다
    public ResponseEntity<?> createStory(@AuthenticationPrincipal String userId, @RequestBody BookDTO dto) {
        try {
            BookEntity bookEntity = BookDTO.toEntity(dto);
            bookEntity.setAuthor(Integer.parseInt(userId));

            // 1. book db에 책을 저장해서 bookId를 채번한다
            BookEntity savedBook = bookService.createBookId(bookEntity);
            Integer bookId = savedBook.getBookId();

            // 2. 각 페이지를 돌며 page db에 책 페이지를 저장한다.
            for (PageDTO pageDTO : dto.getPages()) {
                PageEntity pageEntity = new PageEntity();
                pageEntity.setPageNo(new PageId(bookId, pageDTO.getPageNo()));
                pageEntity.setFullStory(pageDTO.getFullStory());
                pageEntity.setBook(savedBook);

                // 2-1. tts를 생성한다
                saveTtsAudio(savedBook, pageDTO, pageEntity);

                pageService.createPage(pageEntity);
            }

            // 3. bookDTO를 반환한다
            BookDTO savedBookDto = BookDTO.builder()
                    .bookId(savedBook.getBookId())
                    .author(savedBook.getAuthor())
                    .build();

            return ResponseEntity.ok().body(savedBookDto);
        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/create/image")
    // 최종 책 제목, 이미지, 오디오 파일을 저장한다
    public ResponseEntity<?> saveImg(@AuthenticationPrincipal String userId, @RequestBody BookDTO bookDto) {
        try {
            // 기존에 저장된 bookEntity 찾기
            BookEntity originalBook = bookService.retrieveByBookId(bookDto.getBookId());

            // 1. 각 페이지를 돌며 page 저장
            List<PageDTO> updatedPages = new ArrayList<>();
            for (PageDTO pageDto : bookDto.getPages()) {
                // 1-0. 해당하는 page를 찾아온다
                PageEntity originalPage = pageService.retrieveByPageId(new PageId(bookDto.getBookId(), pageDto.getPageNo()));
                // 1-1. 이미지
                saveOriginalBookImage(originalBook, pageDto, originalPage);
                // 1-2. 이미지를 pages에 저장한다.
                pageService.updatePage(originalPage);
                // 1-4. 업데이트된 PageDTO를 생성하여 리스트에 추가한다.
                PageDTO updatedPageDto = PageDTO.builder()
                        .pageNo(pageDto.getPageNo())
                        .fullStory(pageDto.getFullStory())
                        .originalImageUrl(originalPage.getOriginalImageUrl())
                        .finalImageUrl(originalPage.getFinalImageUrl())
                        .audioUrl(originalPage.getAudioUrl())
                        .build();
                updatedPages.add(updatedPageDto);
            }

            // 3. bookEntity를 db에 저장한다
            bookService.updateTitleStoryAudio(originalBook);

            // 4. bookDTO를 반환한다
            BookDTO savedBookDto = BookDTO.builder()
                    .bookId(originalBook.getBookId())
                    .author(originalBook.getAuthor())
                    .title(originalBook.getTitle())
                    .thumbnailUrl(originalBook.getThumbnailUrl())
                    .pages(updatedPages)
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
    public ResponseEntity<?> saveFinalImgAndAudio(@AuthenticationPrincipal String userId, @RequestBody BookDTO bookDto) {
        try {
            // 기존에 저장된 bookEntity 찾기
            BookEntity originalBook = bookService.retrieveByBookId(bookDto.getBookId());

            // 0. 최종 제목 저장
            originalBook.setTitle(bookDto.getTitle());

            // 0-1. 제목을 토대로 표지를 생성해서 저장한다.
            originalBook.setThumbnailUrl(thumbnailService.createThumbnail(originalBook));

            // 1. 각 페이지를 돌며 page 저장
            List<PageDTO> updatedPages = new ArrayList<>();
            for (PageDTO pageDto : bookDto.getPages()) {
                // 1-0. 해당하는 page를 찾아온다
                PageEntity originalPage = pageService.retrieveByPageId(new PageId(bookDto.getBookId(), pageDto.getPageNo()));

                // 1-1. 이미지
                try {
                    String fileName = String.valueOf(originalBook.getBookId()) + "_" + String.valueOf(pageDto.getPageNo());
                    // 1-1-0. 이미지를 바이트 배열로 변환
                    byte[] imageContent = saveImgService.convertBase64ToImage(pageDto.getFinalImageUrl());
                    // 1-1-1. 이미지를 저장경로에 저장한다.
                    String imgUrl = fileService.uploadFile(imageContent, fileName + ".png");
                    // 1-1-2. imgUrl 변수에 경로를 담는다
                    originalPage.setFinalImageUrl(imgUrl);

                    log.info(String.valueOf(pageDto.getPageNo()));

                } catch (Exception e) {
                    throw new RuntimeException("Error converting image: " + e.getMessage(), e);
                }
              
                saveFinalBookImage(originalBook, pageDto, originalPage);

                // 1-2. 이미지랑 오디오를 pages에 저장한다.
                pageService.updatePage(originalPage);

                // 1-3. 업데이트된 PageDTO를 생성하여 리스트에 추가한다.
                PageDTO updatedPageDto = PageDTO.builder()
                        .pageNo(pageDto.getPageNo())
                        .fullStory(pageDto.getFullStory())
                        .originalImageUrl(originalPage.getOriginalImageUrl())
                        .finalImageUrl(originalPage.getFinalImageUrl())
                        .audioUrl(originalPage.getAudioUrl())
                        .build();
                updatedPages.add(updatedPageDto);
            }

            // 3. bookEntity를 db에 저장한다
            bookService.updateTitleStoryAudio(originalBook);

            // 4. bookDTO를 반환한다
            BookDTO savedBookDto = BookDTO.builder()
                    .bookId(originalBook.getBookId())
                    .author(originalBook.getAuthor())
                    .title(originalBook.getTitle())
                    .thumbnailUrl(originalBook.getThumbnailUrl())
                    .pages(updatedPages)
                    .build();

            return ResponseEntity.ok().body(savedBookDto);
        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    private List<PageDTO> getPageDTOS(BookEntity bookEntity) {
        List<PageEntity> pageEntities = pageService.retrieveByBookId(bookEntity.getBookId());

        List<PageDTO> pageDtos = new ArrayList<>();
        for (PageEntity pageEntity : pageEntities) {
            PageDTO pageDto = PageDTO.builder()
                    .pageNo(pageEntity.getPageNo().getPageNo())
                    .fullStory(pageEntity.getFullStory())
                    .originalImageUrl(pageEntity.getOriginalImageUrl())
                    .finalImageUrl(pageEntity.getFinalImageUrl())
                    .audioUrl(pageEntity.getAudioUrl())
                    .userAudioUrl(pageEntity.getUserAudioUrl())
                    .build();
            pageDtos.add(pageDto);
        }
        return pageDtos;
    }

    private void saveTtsAudio(BookEntity originalBook, PageDTO pageDto, PageEntity originalPage) {
        try {
            String fileName = String.valueOf(originalBook.getBookId()) + "_" + String.valueOf(pageDto.getPageNo());
            // 1-2-0. tts를 호출한다
            byte[] audioContent = ttsService.synthesizeText(originalPage.getFullStory(), fileName);
            // 1-2-1. 생성된 오디오파일을 저장경로에 저장한다.
            String audioUrl = fileService.uploadFile(audioContent, fileName + ".mp3");
            // 1-2-2. audioUrl 변수에 경로를 담는다.
            originalPage.setAudioUrl(audioUrl);

        } catch (Exception e) {
            throw new RuntimeException("Error synthesizing text: " + e.getMessage(), e);
        }
    }

    private void saveOriginalBookImage(BookEntity originalBook, PageDTO pageDto, PageEntity originalPage) {

        try {
            String fileName = "original_" + String.valueOf(originalBook.getBookId()) + "_" + String.valueOf(pageDto.getPageNo());
            // 1-1-0. 이미지를 바이트 배열로 변환
            byte[] imageContent = saveImgService.convertBase64ToImage(pageDto.getOriginalImageUrl());
            // 1-1-1. 이미지를 저장경로에 저장한다.
            String imgUrl = fileService.uploadFile(imageContent, fileName + ".png");
            // 1-1-2. imgUrl 변수에 경로를 담는다
            originalPage.setOriginalImageUrl(imgUrl);

            log.info(String.valueOf(pageDto.getPageNo()));

            // 첫 페이지 thumbnailUrl 저장
            if(pageDto.getPageNo() == 1){
                originalBook.setThumbnailUrl(imgUrl);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error converting image: " + e.getMessage(), e);
        }
    }

    private void saveFinalBookImage(BookEntity originalBook, PageDTO pageDto, PageEntity originalPage) {

        try {
            String fileName = String.valueOf(originalBook.getBookId()) + "_" + String.valueOf(pageDto.getPageNo());
            // 1-1-0. 이미지를 바이트 배열로 변환
            byte[] imageContent = saveImgService.convertBase64ToImage(pageDto.getFinalImageUrl());
            // 1-1-1. 이미지를 저장경로에 저장한다.
            String imgUrl = fileService.uploadFile(imageContent, fileName + ".png");
            // 1-1-2. imgUrl 변수에 경로를 담는다
            originalPage.setFinalImageUrl(imgUrl);

            log.info(String.valueOf(pageDto.getPageNo()));

            // 첫 페이지 thumbnailUrl 저장
            if(pageDto.getPageNo() == 1){
                originalBook.setThumbnailUrl(imgUrl);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error converting image: " + e.getMessage(), e);
        }
    }





}
