package jungle.fairyTeller.board.controller;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private CommentService commentService;
    @Autowired
    private UserRepository userRepository;
    // 게시글을 저장한다
    @PostMapping("/save")
    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(@RequestBody BoardDto boardDto, @AuthenticationPrincipal String userId) {
        try {
            UserEntity user = userRepository.findById(Integer.parseInt(userId))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            BoardEntity boardEntity = BoardDto.toEntity(boardDto);

            boardEntity.setAuthor(user.getId());
            boardEntity.setNickname(user.getNickname());

            BookEntity bookEntity = bookRepository.findById(boardEntity.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
            boardEntity.setTitle(bookEntity.getTitle());
            //boardEntity.setThumbnailUrl(bookEntity.getThumbnailUrl());
//            String thumbnailUrl = "https://s3.ap-northeast-2.amazonaws.com/" + bookEntity.getThumbnailUrl();
//            boardEntity.setThumbnailUrl(thumbnailUrl);
//            String audioUrl = "https://s3.ap-northeast-2.amazonaws.com/" + bookEntity.getAudioUrl();
//            boardEntity.setAudioUrl(audioUrl);
            // BookEntity 정보를 가져온 후 BoardDto에 설정
            BoardEntity savedBoard = boardService.saveBoard(boardEntity);
            Pageable pageable = PageRequest.of(0, 9); // 페이지 크기와 정렬 방식을 지정
            ResponseDto<BoardDto> response = getAllBoardsResponse(pageable, userId); // 수정된 부분
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Failed to save the board", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 모든 Board를 반환한다
    @GetMapping
    public ResponseEntity<ResponseDto<BoardDto>> getAllBoards(@AuthenticationPrincipal String userId,
                                                              @PageableDefault(size = 9, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            ResponseDto<BoardDto> response = getAllBoardsResponse(pageable, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("모든 게시물을 불러오는데 실패했습니다", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/paged")
    public Page<BoardEntity> getAllBoardsPaged(Pageable pageable) {
        return boardService.getPagedBoards(pageable);
    }

    // 모든 Board를 반환하는 ResponseDto를 생성한다
    private ResponseDto<BoardDto> getAllBoardsResponse(Pageable pageable, String userId) {
        Page<BoardEntity> boardPage = boardService.getAllBoards(pageable);
        List<BoardEntity> boards = boardPage.getContent();
        List<BoardDto> dtos = boards.stream().map(board -> {
            BoardDto boardDto = new BoardDto(board);
            boolean isEditable = board.getAuthor().equals(Integer.parseInt(userId));
            boardDto.setEditable(isEditable);
            return boardDto;
        }).collect(Collectors.toList());
        return ResponseDto.<BoardDto>builder().data(dtos).build();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardDto>> getBoardById(
            @PathVariable Integer boardId,
            @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal String userId
    ) {
        Optional<BoardEntity> boardOptional = Optional.ofNullable(boardService.getBoardById(boardId));
        return boardOptional.map(board -> {
            BoardDto boardDto = new BoardDto(board);

            // 게시글 작성자와 로그인한 사용자의 ID를 비교하여 수정 가능 여부 판단
            boolean isEditable = board.getAuthor().equals(Integer.parseInt(userId));
            boardDto.setEditable(isEditable);

            Pageable commentPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
            Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardId, commentPageable);
            List<CommentEntity> comments = commentPage.getContent();
            List<CommentDto> commentDtos = comments.stream()
                    .map(comment -> {
                        CommentDto commentDto = new CommentDto(comment);
                        boolean isCommentEditable = comment.getUserId().equals(Integer.parseInt(userId));
                        commentDto.setEditable(isCommentEditable);
                        return commentDto;
                    })
                    .collect(Collectors.toList());
            boardDto.setComments(commentDtos);

            ResponseDto<BoardDto> response = new ResponseDto<>();
            response.setData(Collections.singletonList(boardDto));
            response.setCurrentPage(commentPage.getNumber());
            response.setTotalPages(commentPage.getTotalPages());
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

}
