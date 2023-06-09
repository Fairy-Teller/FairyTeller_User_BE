package jungle.fairyTeller.board.controller;

import jungle.fairyTeller.board.dto.BoardDto;
import jungle.fairyTeller.board.dto.ResponseDto;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;
    // 게시글을 저장한다
    @PostMapping("/save")
    public ResponseEntity<?> saveBoard(@RequestBody BoardDto boardDto) {
        try {
            BoardEntity boardEntity = BoardDto.toEntity(boardDto);
            BoardEntity savedBoard = boardService.saveBoard(boardEntity);
            return ResponseEntity.ok(savedBoard.getBoardId());
        } catch (Exception e) {
            log.error("Failed to save the board", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 모든 Board를 반환한다
    @GetMapping
    public ResponseEntity<ResponseDto<BoardDto>> getAllBoards(@AuthenticationPrincipal String userId) {
        try {
            List<BoardEntity> boards = boardService.getAllBoards();
            List<BoardDto> dtos = boards.stream().map(BoardDto::new).collect(Collectors.toList());
            ResponseDto<BoardDto> response = ResponseDto.<BoardDto>builder().data(dtos).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Failed to retrieve all boards", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
