package jungle.fairyTeller.board.controller;

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
            boardEntity.setNickname(user.getNickname());
            // BookEntity 정보를 가져온 후 BoardDto에 설정
            BookEntity bookEntity = bookRepository.findById(boardEntity.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
            boardEntity.setBookId(bookEntity.getBookId());
            boardEntity.setAuthor(bookEntity.getAuthor());
            boardEntity.setTitle(bookEntity.getTitle());
            boardEntity.setThumbnailUrl(bookEntity.getThumbnailUrl());
            BoardEntity savedBoard = boardService.saveBoard(boardEntity);
            Pageable pageable = PageRequest.of(0, 9); // 페이지 크기와 정렬 방식을 지정
            ResponseDto<BoardDto> response = getAllBoardsResponse(pageable); // 수정된 부분
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
            ResponseDto<BoardDto> response = getAllBoardsResponse(pageable);
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
    private ResponseDto<BoardDto> getAllBoardsResponse(Pageable pageable) {
        Page<BoardEntity> boardPage = boardService.getAllBoards(pageable);
        List<BoardEntity> boards = boardPage.getContent();
        List<BoardDto> dtos = boards.stream().map(BoardDto::new).collect(Collectors.toList());
        return ResponseDto.<BoardDto>builder().data(dtos).build();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardDto>> getBoardById(
            @PathVariable Integer boardId,
            @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Optional<BoardEntity> boardOptional = Optional.ofNullable(boardService.getBoardById(boardId));
        if (boardOptional.isPresent()) {
            BoardEntity board = boardOptional.get();
            BoardDto boardDto = new BoardDto(board);
            Pageable commentPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "commentId");
            Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardId, commentPageable);
            List<CommentEntity> comments = commentPage.getContent();
            List<CommentDto> commentDtos = comments.stream()
                    .map(CommentDto::new)
                    .collect(Collectors.toList());
            boardDto.setComments(commentDtos);

            ResponseDto<BoardDto> response = new ResponseDto<>();
            response.setData(Collections.singletonList(boardDto));
            response.setCurrentPage(commentPage.getNumber());
            response.setTotalPages(commentPage.getTotalPages());
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
