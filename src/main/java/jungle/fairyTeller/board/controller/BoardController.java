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

    @GetMapping
    public ResponseEntity<ResponseDto<BoardDto>> getAllBoards(@AuthenticationPrincipal String userId,
                                                              @PageableDefault(size = 8, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            // Retrieve sorted board page
            Page<BoardEntity> sortedBoardPage = boardService.getAllBoardsPaged(pageable);

            // Convert board entities to DTOs
            List<BoardDto> boardDtos = sortedBoardPage.getContent().stream()
                    .map(boardEntity -> {
                        // Retrieve pages and comments for each board
                        List<PageDTO> pageDTOs = PageDTO.fromEntityList(boardEntity.getBook().getPages());
                        List<CommentDto> commentDtos = CommentDto.fromEntityList(boardEntity.getComments());

                        // Convert board entity to DTO
                        return BoardDto.builder()
                                .boardId(boardEntity.getBoardId())
                                .bookId(boardEntity.getBook().getBookId())
                                .title(boardEntity.getTitle())
                                .description(boardEntity.getDescription())
                                .thumbnailUrl(boardEntity.getThumbnailUrl())
                                .createdDatetime(boardEntity.getCreatedDatetime())
                                .authorId(boardEntity.getAuthor().getId())
                                .nickname(boardEntity.getAuthor().getNickname())
                                .pages(pageDTOs != null ? pageDTOs : new ArrayList<>())  // Check if pages is null
                                .comments(commentDtos != null ? commentDtos : new ArrayList<>())  // Check if comments is null
                                .build();
                    })
                    .collect(Collectors.toList());

            // Response DTO
            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
                    .error(null)
                    .data(boardDtos)
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Failed to retrieve the boards", e);
            throw new ServiceException("Failed to retrieve the boards");
        }
    }


//    @GetMapping
//    public ResponseEntity<ResponseDto<BoardDto>> getAllBoards(@AuthenticationPrincipal String userId,
//                                                              @PageableDefault(size = 9, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {
//        try {
//            ResponseDto<BoardDto> response = getAllBoardsResponse(pageable, userId);
//            return ResponseEntity.ok().body(response);
//        }
//    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@AuthenticationPrincipal String userId, @RequestBody BoardDto boardDto) {
        try {
            BoardEntity savedBoardEntity = boardService.saveBoard(boardDto.getBookId(), userId, boardDto.getDescription());

            // BookEntity의 페이지 정보 가져오기
            BookEntity bookEntity = bookRepository.findById(boardDto.getBookId())
                    .orElseThrow(() -> new ServiceException("Book not found"));
            List<PageEntity> pages = bookEntity.getPages();
            List<PageDTO> pageDTOs = PageDTO.fromEntityList(pages);

            // Retrieve sorted board list
            Sort sort = Sort.by(Sort.Direction.DESC, "boardId");
            Pageable pageable = PageRequest.of(0, 8, sort);
            Page<BoardEntity> sortedBoardPage = boardService.getAllBoardsPaged(pageable);

            // Convert sorted board entities to DTOs
            List<BoardDto> sortedBoardDtos = sortedBoardPage.getContent().stream()
                    .map(this::convertToBoardDto)
                    .collect(Collectors.toList());

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
                    .data(sortedBoardDtos) // Set the sorted board list as data
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Failed to save the board", e);
            throw new ServiceException("Failed to save the board");
        }
    }

    private BoardDto convertToBoardDto(BoardEntity boardEntity) {

        return BoardDto.builder()
                .boardId(boardEntity.getBoardId())
                .bookId(boardEntity.getBook().getBookId())
                .title(boardEntity.getTitle())
                .description(boardEntity.getDescription())
                .thumbnailUrl(boardEntity.getThumbnailUrl())
                .createdDatetime(boardEntity.getCreatedDatetime())
                .authorId(boardEntity.getAuthor().getId())
                .nickname(boardEntity.getAuthor().getNickname())
                .build();
    }


//    @PostMapping("/save")
//    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@AuthenticationPrincipal String userId, @RequestBody BoardDto boardDto) {
//        try {
//
//            BoardEntity savedBoardEntity = boardService.saveBoard(boardDto.getBookId(), userId, boardDto.getDescription());
//
//            // BookEntity의 페이지 정보 가져오기
//            BookEntity bookEntity = bookRepository.findById(boardDto.getBookId())
//                    .orElseThrow(() -> new ServiceException("Book not found"));
//            List<PageEntity> pages = bookEntity.getPages();
//            List<PageDTO> pageDTOs = PageDTO.fromEntityList(pages);
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
//                    .comments(new ArrayList<>())
//                    .pages(pageDTOs)
//                    .build();
//
//            // ResponseDto 생성
//            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
//                    .error(null)
//                    .data(Collections.singletonList(savedBoardDto))
//                    .build();
//
//            return ResponseEntity.ok(responseDto);
//        } catch (Exception e) {
//            log.error("Failed to save the board", e);
//            throw new ServiceException("Failed to save the board");
//        }
//    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardDto>> getBoardById(@PathVariable Integer boardId) {
        try {
            // Retrieve the board entity by boardId
            BoardEntity boardEntity = boardService.getBoardById(boardId);

            // Convert the board entity to DTO
            BoardDto boardDto = BoardDto.builder()
                    .boardId(boardEntity.getBoardId())
                    .bookId(boardEntity.getBook().getBookId())
                    .title(boardEntity.getTitle())
                    .description(boardEntity.getDescription())
                    .thumbnailUrl(boardEntity.getThumbnailUrl())
                    .createdDatetime(boardEntity.getCreatedDatetime())
                    .authorId(boardEntity.getAuthor().getId())
                    .nickname(boardEntity.getAuthor().getNickname())
                    .comments(CommentDto.fromEntityList(boardEntity.getComments()))
                    .build();

            // Response DTO
            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
                    .error(null)
                    .data(Collections.singletonList(boardDto))
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("Failed to retrieve the board", e);
            throw new ServiceException("Failed to retrieve the board");
        }
    }



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
