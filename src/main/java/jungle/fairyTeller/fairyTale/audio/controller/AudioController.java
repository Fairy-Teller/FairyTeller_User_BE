package jungle.fairyTeller.fairyTale.audio.controller;

import jungle.fairyTeller.fairyTale.audio.dto.ResponseDTO;
import jungle.fairyTeller.fairyTale.audio.service.VoiceService;
import jungle.fairyTeller.fairyTale.book.dto.BookDTO;
import jungle.fairyTeller.fairyTale.book.dto.PageDTO;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import jungle.fairyTeller.fairyTale.book.service.BookService;
import jungle.fairyTeller.fairyTale.book.service.PageService;
import jungle.fairyTeller.fairyTale.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/audio")
public class AudioController {

    @Autowired
    private BookService bookService;

    @Autowired
    private PageService pageService;

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private FileService fileService;

    @PostMapping("/user-record")
    public ResponseEntity<?> saveUserVoice(@RequestBody BookDTO dto, @AuthenticationPrincipal String userId) {
        try {
            // 기존에 저장된 bookEntity 찾기
            BookEntity originalBook = bookService.retrieveByBookId(dto.getBookId());

            // 오디오 파일
            String fileName = String.valueOf(originalBook.getBookId());
            // 오디오 파일을 저장경로에 저장한다
            List<PageDTO> pages = dto.getPages();
            for (PageDTO page : pages) {
                try {
                    Integer pageNo = page.getPageNo();
                    // 1-0. 기존에 저장된 pageEntity 찾기
                    PageEntity originalPage = pageService.retrieveByBookId(dto.getBookId(), pageNo);

                    // 1-1. 요청으로 넘어온 음성을 바이트 배열로 변환
                    String audioBeforeConvert = page.getAudioUrl();
                    byte[] audioContent = voiceService.convertBase64ToAudio(audioBeforeConvert);

                    // 1-2. Base64 음성을 파일로 변환하여 저장
                    String audioUrl = fileService.uploadFile(audioContent, fileName + "_" + pageNo + "_custom.mp3");

                    // 1-3. audioUrl 변수에 경로를 담는다.
                    originalPage.setAudioUrl(audioUrl);

                    // 1-4. pageEntity를 db에 저장한다
                    pageService.updateUserAudio(originalPage);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            BookDTO updatedBookDto = BookDTO.builder()
                    .bookId(originalBook.getBookId())
                    .title(originalBook.getTitle())
                    .thumbnailUrl(originalBook.getThumbnailUrl())
                    .pages(originalBook.getPages().stream().map(PageDTO::new).collect(Collectors.toList()))
                    .build();

            return ResponseEntity.ok().body(updatedBookDto);
        } catch(Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
