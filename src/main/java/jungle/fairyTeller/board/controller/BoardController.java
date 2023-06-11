package jungle.fairyTeller.board.controller;

import jungle.fairyTeller.board.dto.BoardDto;
import jungle.fairyTeller.board.dto.CommentDto;
import jungle.fairyTeller.board.dto.ResponseDto;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.entity.CommentEntity;
import jungle.fairyTeller.board.service.BoardService;
import jungle.fairyTeller.board.service.CommentService;
import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
            BoardEntity savedBoard = boardService.saveBoard(boardEntity);
            ResponseDto<BoardDto> response = getAllBoardsResponse();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Failed to save the board", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 모든 Board를 반환한다
    @GetMapping
    public ResponseEntity<ResponseDto<BoardDto>> getAllBoards(@AuthenticationPrincipal String userId) {
        try {
            ResponseDto<BoardDto> response = getAllBoardsResponse();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Failed to retrieve all boards", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 모든 Board를 반환하는 ResponseDto를 생성한다
    private ResponseDto<BoardDto> getAllBoardsResponse() {
        List<BoardEntity> boards = boardService.getAllBoards();
        List<BoardDto> dtos = boards.stream().map(BoardDto::new).collect(Collectors.toList());
        return ResponseDto.<BoardDto>builder().data(dtos).build();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardDto>> getBoardById(@PathVariable Integer boardId) {
        Optional<BoardEntity> boardOptional = Optional.ofNullable(boardService.getBoardById(boardId));
        if (boardOptional.isPresent()) {
            BoardEntity board = boardOptional.get();
            BoardDto boardDto = new BoardDto(board);

            List<CommentEntity> comments = commentService.getCommentsByBoardId(boardId);
            List<CommentDto> commentDtos = comments.stream()
                    .map(CommentDto::new)
                    .collect(Collectors.toList());
            boardDto.setComments(commentDtos);

            ResponseDto<BoardDto> response = new ResponseDto<>();
            response.setData(Collections.singletonList(boardDto));
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{boardId}/comment")
    public ResponseEntity<ResponseDto<CommentDto>> saveComment(@PathVariable Integer boardId, @RequestBody CommentDto commentDto, @AuthenticationPrincipal String userId) {
        try {
            UserEntity user = userRepository.findById(Integer.parseInt(userId))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            CommentEntity commentEntity = CommentDto.toEntity(commentDto);
            commentEntity.setBoardId(boardId);
            commentEntity.setAuthor(user.getNickname()); // Set the author as the user's nickname
            CommentEntity savedComment = commentService.saveComment(commentEntity);
            CommentDto savedCommentDto = new CommentDto(savedComment);
            ResponseDto<CommentDto> response = new ResponseDto<>();
            response.setData(Collections.singletonList(savedCommentDto));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Failed to save the comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
