package jungle.fairyTeller.fairyTale.book.controller;

import jungle.fairyTeller.fairyTale.Image.service.SaveImgService;
import jungle.fairyTeller.fairyTale.Image.service.ThumbnailService;
import jungle.fairyTeller.fairyTale.book.dto.ObjectDTO;
import jungle.fairyTeller.fairyTale.book.mapper.ObjectMapper;
import jungle.fairyTeller.fairyTale.book.service.PageObjectService;
import jungle.fairyTeller.fairyTale.audio.service.TtsService;
import jungle.fairyTeller.fairyTale.book.dto.BookDTO;
import jungle.fairyTeller.fairyTale.book.dto.PageDTO;
import jungle.fairyTeller.fairyTale.book.dto.ResponseDTO;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageId;
import jungle.fairyTeller.fairyTale.book.entity.PageObjectEntity;
import jungle.fairyTeller.fairyTale.book.service.BookService;
import jungle.fairyTeller.fairyTale.book.service.PageService;
import jungle.fairyTeller.fairyTale.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.security.Security;
import java.util.*;
import java.util.List;
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
    private PageObjectService pageObjectService;

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
                .thumbnailUrl(bookEntity.getThumbnailUrl())
                .pages(pageDtos)
                .build();

        return ResponseEntity.ok().body(dto);
    }
    @PostMapping("/getBookById/temp")
    public ResponseEntity<?> getTempBookByBookId(@RequestBody BookDTO bookDTO,@AuthenticationPrincipal String userId) {
        BookEntity bookEntity = bookService.getBookByBookId(bookDTO.getBookId());

        List<PageDTO> pageDtos = getPageDTOS(bookEntity);

        BookDTO dto = BookDTO.builder()
                .bookId(bookEntity.getBookId())
                .imageFinal(bookEntity.isImageFinal())
                .author(bookEntity.getAuthor())
                .title(bookEntity.getTitle())
                .thumbnailUrl(bookEntity.getThumbnailUrl())
                .theme(bookEntity.getTheme())
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
                    .thumbnailUrl(bookEntity.getThumbnailUrl())
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
            bookEntity.setLastModifiedDate(new Date());

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
                new Thread(() -> {
                    // TTS 호출 메소드 실행
                    saveTtsAudio(savedBook, pageDTO, pageEntity);
                    pageService.createPage(pageEntity);
                }).start();

//                saveTtsAudio(savedBook, pageDTO, pageEntity);

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

    @PostMapping("/create/theme")
    public ResponseEntity<?> saveTheme(@AuthenticationPrincipal String userId, @RequestBody BookDTO bookDto) {
        try {
            // 기존에 저장된 bookEntity 찾기
            BookEntity originalBook = bookService.retrieveByBookId(bookDto.getBookId());

            // 이미지 테마를 업데이트한다
            originalBook.setTheme(bookDto.getTheme());
            bookService.updateTheme(originalBook);

            BookDTO savedBookDto = BookDTO.builder()
                    .bookId(originalBook.getBookId())
                    .author(originalBook.getAuthor())
                    .theme(originalBook.getTheme())
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

                // 1-3. 이미지 isDark 판별
                byte[] imageContent = saveImgService.convertBase64ToImage(pageDto.getOriginalImageUrl());
                boolean isDark = saveImgService.isImageDark(imageContent);

                // 1-4. 업데이트된 PageDTO를 생성하여 리스트에 추가한다.
                PageDTO updatedPageDto = PageDTO.builder()
                        .pageNo(pageDto.getPageNo())
                        .fullStory(pageDto.getFullStory())
                        .originalImageUrl(originalPage.getOriginalImageUrl())
                        .finalImageUrl(originalPage.getFinalImageUrl())
                        .audioUrl(originalPage.getAudioUrl())
                        .isDark(isDark)
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

    @PostMapping("/create/imageAll")
    public ResponseEntity<?> selectedAllImg(@AuthenticationPrincipal String userId, @RequestBody BookDTO bookDto) {
        BookEntity originalBook = bookService.retrieveByBookId(bookDto.getBookId());

        originalBook.setImageFinal(true);
        originalBook.setTitle("임시저장_"+originalBook.getBookId());

        // 첫번째 이미지를 thumbnail url로 저장한다
        PageEntity firstPage = pageService.retrieveByPageId(new PageId(originalBook.getBookId(), 1));
        originalBook.setThumbnailUrl(firstPage.getOriginalImageUrl());

        originalBook.setLastModifiedDate(new Date());

        bookService.updateTitleStoryAudio(originalBook);

        BookDTO savedBookDto = BookDTO.builder()
                .bookId(originalBook.getBookId())
                .author(originalBook.getAuthor())
                .title(originalBook.getTitle())
                .thumbnailUrl(originalBook.getThumbnailUrl())
                .build();

        return ResponseEntity.ok().body(savedBookDto);
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
            String thumbanailUrl = thumbnailService.createThumbnail(originalBook);

            originalBook.setThumbnailUrl(thumbanailUrl);

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

                    // 이미지 사이즈 1280x720로 조정
                    BufferedImage resizedImage = resizeImage(imageContent, 1280, 720);
                    byte[] resizedImageContent = convertImageToBytes(resizedImage);

                    // 1-1-1. 이미지를 저장경로에 저장한다.
                    String imgUrl = fileService.uploadFile(resizedImageContent, fileName + ".png");
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

            // 최종 edit 상태 true로 저장
            originalBook.setEditFinal(true);
            originalBook.setLastModifiedDate(new Date());

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

    @GetMapping("/find/newestTemp")
    public ResponseEntity<?> findNewestTemporaryStorage(@AuthenticationPrincipal String userId){

        try {
            //1.userId가 만든 books 중 edit_final = false이면서 가장 최신 날짜를 조회한다.
            int id = Integer.parseInt(userId);
            int tmpStorageCount = bookService.countByAuthorAndEditFinal(id);

            log.info("check for temporary storage id: {}",id);

            if(tmpStorageCount == 0){
                return ResponseEntity.ok(null);
            }
            List<BookEntity> lists = bookService.getLatestBookByAuthor(id);
            List<Map<String, String>> books = new ArrayList<>();

            for (BookEntity book : lists) {
                Map<String, String> map = new HashMap<>();
                List<PageDTO> pageDTOS = getPageDTOS(book);

                // 페이지 내용을 저장할 문자열
                StringBuilder pageContents = new StringBuilder();

                for (int i = 0; i < pageDTOS.size(); i++) {
                    PageDTO page = pageDTOS.get(i);
                    pageContents.append(page.getFullStory());

                    // 마지막 페이지가 아닌 경우에는 쉼표로 구분하여 추가합니다.
                    if (i < pageDTOS.size() - 1) {
                        pageContents.append(", ");
                    }
                }

                map.put("pages", pageContents.toString());
                map.put("bookId", String.valueOf(book.getBookId()));
                map.put("lastModifiedDate", book.getLastModifiedDate() != null ? book.getLastModifiedDate().toString() : "null");

                books.add(map);
            }
            return ResponseEntity.ok().body(books);
//            if(!bookEntity.isImageFinal()){
//                // 2-1. image_final false 인 경우 => image_generate 로 넘어감, mongoDB 조회 X
//                List<PageDTO> pageDTOS = getPageDTOS(bookEntity);
//
//                BookDTO dto = BookDTO.builder()
//                        .bookId(bookEntity.getBookId())
//                        .imageFinal(bookEntity.isImageFinal())
//                        .author(bookEntity.getAuthor())
//                        .title(bookEntity.getTitle())
//                        .thumbnailUrl(bookEntity.getThumbnailUrl())
//                        .theme(bookEntity.getTheme())
//                        .pages(pageDTOS)
//                        .build();
//                return ResponseEntity.ok().body(dto);
//            }

//            if(!bookEntity.isImageFinal()){
//                // 2-1. image_final false 인 경우 => image_generate 로 넘어감, mongoDB 조회 X
//                List<PageDTO> pageDTOS = getPageDTOS(bookEntity);
//
//                BookDTO dto = BookDTO.builder()
//                        .bookId(bookEntity.getBookId())
//                        .imageFinal(bookEntity.isImageFinal())
//                        .author(bookEntity.getAuthor())
//                        .title(bookEntity.getTitle())
//                        .thumbnailUrl(bookEntity.getThumbnailUrl())
//                        .theme(bookEntity.getTheme())
//                        .pages(pageDTOS)
//                        .build();
//                return ResponseEntity.ok().body(dto);
//            }else{
//                // 2-2. image_final true 인 경우 => editor 로 넘어감, mongoDB 조회 O
//                List<PageDTO> pageDTOS = getPageDTOS(bookEntity);
//
//                for(PageDTO pageDTO : pageDTOS){
//                    //해당 bookId와 pageNo로 mongoDB에서 가져오기
//                    PageId pageId = new PageId(bookEntity.getBookId(),pageDTO.getPageNo());
//                    List<PageObjectEntity> objects = pageObjectService.findById(pageId);
//                    for(PageObjectEntity object : objects){
//                        pageDTO.setObjects(object.getObjects());
//                    }
//                }
//
//                BookDTO dto = BookDTO.builder()
//                        .bookId(bookEntity.getBookId())
//                        .imageFinal(bookEntity.isImageFinal())
//                        .author(bookEntity.getAuthor())
//                        .title(bookEntity.getTitle())
//                        .thumbnailUrl(bookEntity.getThumbnailUrl())
//                        .theme(bookEntity.getTheme())
//                        .pages(pageDTOS)
//                        .build();
//                return ResponseEntity.ok().body(dto);
//            }
        }catch(Exception e){
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/create/temp")
    public ResponseEntity<?> saveTempPosition(@AuthenticationPrincipal String userId, @RequestBody BookDTO bookDto) {
        try {
            // 기존에 저장된 bookEntity 찾기
            BookEntity originalBook = bookService.retrieveByBookId(bookDto.getBookId());

            // 1. 각 페이지를 돌며 page 저장
            List<PageDTO> updatedPages = new ArrayList<>();
            for (PageDTO pageDto : bookDto.getPages()) {
                // 1-0. 해당하는 page를 찾아온다
                PageEntity originalPage = pageService.retrieveByPageId(new PageId(bookDto.getBookId(), pageDto.getPageNo()));

                // save fabric.js objects to MongoDB
                if(pageDto.getObjects() != null){
                    PageId pageId = new PageId(bookDto.getBookId(), pageDto.getPageNo());

                    Object objects = pageDto.getObjects();

                    PageObjectEntity pageObjectEntity = new PageObjectEntity(pageId, objects);
                    pageObjectService.saveObjects(pageObjectEntity);
                }

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

            return ResponseEntity.ok().body(null);
        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/find/temp")
    public ResponseEntity<?> findObjects(@RequestBody BookDTO bookDto, @AuthenticationPrincipal
                                                String userId){
        try{
            int bookId = bookDto.getBookId();
            BookEntity originalBook = bookService.retrieveByBookId(bookDto.getBookId());

            List<PageDTO> pageDTOS = getPageDTOS(originalBook);

            for(PageDTO pageDTO : pageDTOS){
                //해당 bookId와 pageNo로 mongoDB에서 가져오기
                PageId id = new PageId(bookId,pageDTO.getPageNo());
                List<PageObjectEntity> objects = pageObjectService.findById(id);

                for(PageObjectEntity object : objects){

                    Object dto = object.getObjects();

                    pageDTO.setObjects(dto);
                }
            }
            BookDTO dto = BookDTO.builder()
                    .bookId(originalBook.getBookId())
                    .author(originalBook.getAuthor())
                    .title(originalBook.getTitle())
                    .theme(originalBook.getTheme())
                    .pages(pageDTOS)
                    .imageFinal(originalBook.isImageFinal())
                    .build();

            return ResponseEntity.ok().body(dto);

        }catch (Exception e){
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

        } catch (Exception e) {
            throw new RuntimeException("Error converting image: " + e.getMessage(), e);
        }
    }

    private BufferedImage resizeImage(byte[] imageContent, int targetWidth, int targetHeight) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageContent);
            BufferedImage originalImage = ImageIO.read(bais);

            Image resizedImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);

            BufferedImage bufferedResizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedResizedImage.createGraphics();
            g2d.drawImage(resizedImage, 0, 0, null);
            g2d.dispose();

            return bufferedResizedImage;
        } catch (Exception e) {
            throw new RuntimeException("Error resizing image: " + e.getMessage(), e);
        }
    }

    private byte[] convertImageToBytes(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error converting image to bytes: " + e.getMessage(), e);
        }
    }






}
