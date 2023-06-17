package jungle.fairyTeller.board.controller;
import com.amazonaws.services.kms.model.NotFoundException;
import jungle.fairyTeller.board.repository.BoardRepository;
import jungle.fairyTeller.fairyTale.book.dto.PageDTO;
import jungle.fairyTeller.fairyTale.book.entity.PageEntity;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jungle.fairyTeller.board.dto.BoardDto;
import jungle.fairyTeller.board.dto.CommentDto;
import jungle.fairyTeller.board.dto.ResponseDto;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.entity.CommentEntity;
import jungle.fairyTeller.board.service.BoardService;
import jungle.fairyTeller.board.service.CommentService;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.repository.BookRepository;
import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/board")
public class BoardController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    private BoardService boardService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/save")
    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@AuthenticationPrincipal String userId, @RequestBody BoardDto boardDto) {
        try {

            BoardEntity savedBoardEntity = boardService.saveBoard(boardDto.getBookId(), userId, boardDto.getDescription());

            // BookEntity의 페이지 정보 가져오기
            BookEntity bookEntity = bookRepository.findById(boardDto.getBookId())
                    .orElseThrow(() -> new ServiceException("Book not found"));
            List<PageEntity> pages = bookEntity.getPages();
            List<PageDTO> pageDTOs = PageDTO.fromEntityList(pages);

            // BoardDto로 변환
            BoardDto savedBoardDto = BoardDto.builder()
                    .boardId(savedBoardEntity.getBoardId())
                    .bookId(savedBoardEntity.getBook().getBookId())
                    .title(savedBoardEntity.getTitle())
                    .description(savedBoardEntity.getDescription())
                    .thumbnailUrl(savedBoardEntity.getThumbnailUrl())
                    .createdDatetime(savedBoardEntity.getCreatedDatetime())
                    .authorId(savedBoardEntity.getAuthor().getId())
                    .nickname(savedBoardEntity.getAuthor().getNickname())
                    .comments(new ArrayList<>())
                    .pages(pageDTOs)
                    .build();

            // ResponseDto 생성
            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
                    .error(null)
                    .data(Collections.singletonList(savedBoardDto))
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Failed to save the board", e);
            throw new ServiceException("Failed to save the board");
        }
    }


//    @PostMapping("/save")
//    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@AuthenticationPrincipal String userId, @RequestBody BoardDto boardDto) {
//        try {
//            // BookEntity 조회
//            BookEntity bookEntity = bookRepository.findById(boardDto.getBookId())
//                    .orElseThrow(() -> new ServiceException("Book not found"));
//
//            // UserEntity 조회
//            UserEntity userEntity = userRepository.findById(Integer.parseInt(userId))
//                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//            // 필요한 정보 추출
//            String title = bookEntity.getTitle();
//            String thumbnailUrl = bookEntity.getThumbnailUrl();
//
//            // BoardEntity 생성
//            BoardEntity boardEntity = new BoardEntity();
//            boardEntity.setTitle(title);
//            boardEntity.setThumbnailUrl(thumbnailUrl);
//            boardEntity.setDescription(boardDto.getDescription());
//
//            // 관계 설정
//            boardEntity.setBook(bookEntity);
//            boardEntity.setAuthor(userEntity);
//            boardEntity.setPages(bookEntity.getPages());
//
//            // BoardEntity 저장
//            BoardEntity savedBoardEntity = boardRepository.save(boardEntity);
//
//            // BoardDto로 변환
//            BoardDto savedBoardDto = BoardDto.builder()
//                    .boardId(savedBoardEntity.getBoardId())
//                    .bookId(savedBoardEntity.getBook().getBookId())
//                    .title(savedBoardEntity.getTitle())
//                    .description(savedBoardEntity.getDescription())
//                    .thumbnailUrl(savedBoardEntity.getThumbnailUrl())
//                    .createdDatetime(savedBoardEntity.getCreatedDatetime())
//                    .authorId(savedBoardEntity.getAuthor().getId())
//                    .nickname(savedBoardEntity.getAuthor().getNickname())
//                    .pages(PageDTO.toDtoList(savedBoardEntity.getPages()))
//                    .comments(new ArrayList<>())
//                    .build();
//
//            // ResponseDto 생성
//            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
//                    .error(null)
//                    .data(savedBoardDto)
//                    .build();
//
//            return ResponseEntity.ok(responseDto);
//        } catch (Exception e) {
//            log.error("Failed to save the board", e);
//            throw new ServiceException("Failed to save the board");
//        }
//    }

//    @PostMapping("/save")
//    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@AuthenticationPrincipal String userId, @RequestBody BoardDto boardDto) {
//        Integer bookId = boardDto.getBookId();
//        String description = boardDto.getDescription();
//        BoardEntity savedBoardEntity = boardService.saveBoard(bookId, userId, description);
//        BoardDto savedBoardDto = new BoardDto(savedBoardEntity);
//
//        UserEntity user = userRepository.findById(Integer.parseInt(userId))
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//        savedBoardDto
//
//        BoardEntity boardEntity = BoardDto.toEntity(boardDto);
//        boardEntity.setAuthorId(user.getId());
//        boardEntity.setNickname(user.getNickname());
//
//        // boardEntity에 필요한 정보 설정
//        // ...
//
//        // BoardService를 사용하여 boardEntity를 저장
//        BoardEntity savedBoardEntity = boardService.saveBoard(boardEntity);
//
//        // 저장된 boardEntity를 BoardDto로 변환
//        //BoardDto savedBoardDto = BoardDto.fromEntity(savedBoardEntity);
//
//        // ResponseDto 생성
//        ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
//                .error(null)
//                .data(savedBoardDto)
//                .build();
//
//        // ResponseEntity를 사용하여 HTTP 응답 반환
//        return ResponseEntity.ok(responseDto);
//    }

//    @PostMapping("/save")
//    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@AuthenticationPrincipal String userId, @RequestBody BoardDto boardDto) {
//        try {
//            // 요청 데이터를 유효성 검사하고 처리합니다.
//
//            // userId를 기반으로 사용자 정보를 가져옵니다.
//            UserEntity userEntity = userRepository.findByUserId(userId);
//            if (userEntity == null) {
//                // 사용자를 찾을 수 없을 경우 오류 응답을 반환합니다.
//                ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
//                        .error("사용자를 찾을 수 없습니다.")
//                        .build();
//                return ResponseEntity.badRequest().body(responseDto);
//            }
//
//            // 요청 데이터와 사용자 정보를 사용하여 새로운 BoardEntity를 생성합니다.
//            BoardEntity boardEntity = BoardEntity.builder()
//                    .book(BookEntity.builder().bookId(boardDto.getBookId()).build())
//                    .title(boardDto.getTitle())
//                    .description(boardDto.getDescription())
//                    .thumbnailUrl(boardDto.getThumbnailUrl())
//                    .createdDatetime(new Date())
//                    .authorId(userEntity.getUserId())
//                    .nickname(userEntity.getNickname())
//                    .build();
//
//            // boardEntity를 데이터베이스에 저장합니다.
//            BoardEntity savedBoardEntity = boardRepository.save(boardEntity);
//
//            // 저장된 boardEntity를 BoardDto로 변환합니다.
//            BoardDto savedBoardDto = BoardDto.fromEntity(savedBoardEntity);
//
//            // 응답 DTO를 구성합니다.
//            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
//                    .data(savedBoardDto)
//                    .build();
//
//            // 성공적인 응답을 반환합니다.
//            return ResponseEntity.ok(responseDto);
//        } catch (Exception e) {
//            // 예외를 처리하고 오류 응답을 반환합니다.
//            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
//                    .error(e.getMessage())
//                    .build();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
//        }
//    }


//    @PostMapping("/save")
//    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@AuthenticationPrincipal String userId, @RequestBody BoardDto boardDto) {
//        // userId
//        UserEntity userEntity = userRepository.findById(Integer.parseInt(userId))
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//        String nickname = userEntity.getNickname();
//        // BoardEntity를 생성
//        BoardEntity boardEntity = BoardEntity.builder()
//                .book(BookEntity.builder().bookId(boardDto.getBookId()).build())
//                .title(boardDto.getTitle())
//                .description(boardDto.getDescription())
//                .thumbnailUrl(boardDto.getThumbnailUrl())
//                .createdDatetime(new Date())
//                .authorId(userEntity.getUserId())
//                .nickname(userEntity.getNickname())
//                .build();
//
//
//
//
//
//
//        // 클라이언트에서 전달한 bookId
//        Integer bookId = boardDto.getBookId();
//        // bookId
//        BookEntity bookEntity = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));
//        // boardDto와 가져온 book 정보, 사용자 정보를 이용하여 BoardEntity 생성 및 저장하는 로직 구현
//
//        // 생성된 board를 BoardDto로 변환하여 ResponseDto에 담는 로직 구현
//        ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
//                .error(null)
//                .data(savedBoardDto)
//                .build();
//
//        // 예시 코드에서는 ResponseEntity를 사용하여 HTTP 상태 코드와 함께 응답을 보내도록 하였습니다.
//        return ResponseEntity.ok(responseDto);
//    }

    // 게시글을 저장한다
//    @PostMapping("/save")
//    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@RequestBody BoardDto boardDto, @AuthenticationPrincipal String userId) {
//        try {
//            UserEntity user = userRepository.findById(Integer.parseInt(userId))
//                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
//            BoardEntity boardEntity = BoardDto.toEntity(boardDto);
//
//            boardEntity.setAuthor(user.getId());
//            boardEntity.setNickname(user.getNickname());
//
//            BookEntity bookEntity = bookRepository.findById(boardEntity.getBookId())
//                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
//            boardEntity.setTitle(bookEntity.getTitle());
//            //boardEntity.setThumbnailUrl(bookEntity.getThumbnailUrl());
////            String thumbnailUrl = "https://s3.ap-northeast-2.amazonaws.com/" + bookEntity.getThumbnailUrl();
////            boardEntity.setThumbnailUrl(thumbnailUrl);
////            String audioUrl = "https://s3.ap-northeast-2.amazonaws.com/" + bookEntity.getAudioUrl();
////            boardEntity.setAudioUrl(audioUrl);
//            // BookEntity 정보를 가져온 후 BoardDto에 설정
//            BoardEntity savedBoard = boardService.saveBoard(boardEntity);
//            Pageable pageable = PageRequest.of(0, 9); // 페이지 크기와 정렬 방식을 지정
//            ResponseDto<BoardDto> response = getAllBoardsResponse(pageable, userId); // 수정된 부분
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            log.error("Failed to save the board", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    // 모든 Board를 반환한다
//    @GetMapping
//    public ResponseEntity<ResponseDto<BoardDto>> getAllBoards(@AuthenticationPrincipal String userId,
//                                                              @PageableDefault(size = 9, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {
//        try {
//            ResponseDto<BoardDto> response = getAllBoardsResponse(pageable, userId);
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            log.error("모든 게시물을 불러오는데 실패했습니다", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//    @GetMapping("/paged")
//    public Page<BoardEntity> getAllBoardsPaged(Pageable pageable) {
//        return boardService.getPagedBoards(pageable);
//    }
//
//    // 모든 Board를 반환하는 ResponseDto를 생성한다
//    private ResponseDto<BoardDto> getAllBoardsResponse(Pageable pageable, String userId) {
//        Page<BoardEntity> boardPage = boardService.getAllBoards(pageable);
//        List<BoardEntity> boards = boardPage.getContent();
//        List<BoardDto> dtos = boards.stream().map(board -> {
//            BoardDto boardDto = new BoardDto(board);
//            boolean isEditable = board.getAuthor().equals(Integer.parseInt(userId));
//            boardDto.setEditable(isEditable);
//            return boardDto;
//        }).collect(Collectors.toList());
//        return ResponseDto.<BoardDto>builder().data(dtos).build();
//    }
//
//    @GetMapping("/{boardId}")
//    public ResponseEntity<ResponseDto<BoardDto>> getBoardById(
//            @PathVariable Integer boardId,
//            @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable,
//            @AuthenticationPrincipal String userId
//    ) {
//        Optional<BoardEntity> boardOptional = Optional.ofNullable(boardService.getBoardById(boardId));
//        return boardOptional.map(board -> {
//            BoardDto boardDto = new BoardDto(board);
//
//            // 게시글 작성자와 로그인한 사용자의 ID를 비교하여 수정 가능 여부 판단
//            boolean isEditable = board.getAuthor().equals(Integer.parseInt(userId));
//            boardDto.setEditable(isEditable);
//
//            Pageable commentPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
//            Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardId, commentPageable);
//            List<CommentEntity> comments = commentPage.getContent();
//            List<CommentDto> commentDtos = comments.stream()
//                    .map(comment -> {
//                        CommentDto commentDto = new CommentDto(comment);
//                        boolean isCommentEditable = comment.getUserId().equals(Integer.parseInt(userId));
//                        commentDto.setEditable(isCommentEditable);
//                        return commentDto;
//                    })
//                    .collect(Collectors.toList());
//            boardDto.setComments(commentDtos);
//
//            ResponseDto<BoardDto> response = new ResponseDto<>();
//            response.setData(Collections.singletonList(boardDto));
//            response.setCurrentPage(commentPage.getNumber());
//            response.setTotalPages(commentPage.getTotalPages());
//            return ResponseEntity.ok(response);
//        }).orElse(ResponseEntity.notFound().build());
//    }

}
