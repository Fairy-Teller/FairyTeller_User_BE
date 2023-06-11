package jungle.fairyTeller.board.controller;

import jungle.fairyTeller.board.dto.CommentDto;
import jungle.fairyTeller.board.dto.ResponseDto;
import jungle.fairyTeller.board.entity.CommentEntity;
import jungle.fairyTeller.board.service.BoardService;
import jungle.fairyTeller.board.service.CommentService;
import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/board")
public class CommentController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/{boardId}/comment")
    public ResponseEntity<ResponseDto<CommentDto>> saveComment(@PathVariable Integer boardId, @RequestBody CommentDto commentDto, @AuthenticationPrincipal String userId) {
        try {
            UserEntity user = userRepository.findById(Integer.parseInt(userId))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            CommentEntity commentEntity = CommentDto.toEntity(commentDto);
            commentEntity.setBoardId(boardId);
            commentEntity.setUserId(user.getId());
            commentEntity.setAuthor(user.getNickname()); // Set the author as the user's nickname
            CommentEntity savedComment = commentService.saveComment(commentEntity);
            CommentDto savedCommentDto = new CommentDto(savedComment);
            ResponseDto<CommentDto> response = new ResponseDto<>();
            response.setData(Collections.singletonList(savedCommentDto));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{boardId}/comments")
    public ResponseEntity<ResponseDto<CommentDto>> getCommentsByBoardIdPaged(
            @PathVariable Integer boardId,
            @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        try {
            Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardId, pageable);
            List<CommentEntity> comments = commentPage.getContent();
            List<CommentDto> commentDtos = comments.stream()
                    .map(CommentDto::new)
                    .collect(Collectors.toList());

            ResponseDto<CommentDto> response = new ResponseDto<>();
            response.setData(commentDtos);
            response.setCurrentPage(commentPage.getNumber());
            response.setTotalPages(commentPage.getTotalPages());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
