package jungle.fairyTeller.board.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/board")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
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
            commentEntity.setAuthor(user.getNickname());
            CommentEntity savedComment = commentService.saveComment(commentEntity);
            CommentDto savedCommentDto = new CommentDto(savedComment);
            savedCommentDto.setEditable(savedComment.getUserId().equals(Integer.parseInt(userId)));
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
            @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal String userId
    ) {
        try {
            Integer userIdInt = Integer.parseInt(userId);
            Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardId, pageable);
            List<CommentEntity> comments = commentPage.getContent();
            List<CommentDto> commentDtos = comments.stream()
                    .map(comment -> {
                        CommentDto commentDto = new CommentDto(comment);
                        boolean isEditable = comment.getUserId().equals(userIdInt);
                        commentDto.setEditable(isEditable);
                        logger.info("userId: {}, comment.getUserId(): {}, isEditable: {}", userIdInt, comment.getUserId(), isEditable);
                        return commentDto;
                    })
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

    @DeleteMapping("/{boardId}/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer boardId,
            @PathVariable Integer commentId,
            @AuthenticationPrincipal String userId
    ) {
        try {
            // 댓글 삭제를 위한 권한 확인
            CommentEntity comment = commentService.getCommentById(commentId)
                    .orElseThrow(() -> new NoSuchElementException("Comment not found"));
            // 현재 인증된 사용자와 댓글 작성자 비교
            if (!comment.getUserId().equals(Integer.parseInt(userId))) {
                // 권한이 없는 경우 403 Forbidden 상태 코드 반환
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            // 댓글 삭제
            commentService.deleteComment(commentId);

            // 댓글 삭제가 성공적으로 이루어졌을 경우, 204 No Content 상태 코드를 반환
            return ResponseEntity.ok(comment);
        } catch (NoSuchElementException e) {
            // 댓글이 존재하지 않는 경우 404 Not Found 상태 코드 반환
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to delete the comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{boardId}/comment/{commentId}")
    public ResponseEntity<ResponseDto<CommentDto>> updateComment(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer boardId,
            @PathVariable Integer commentId,
            @RequestBody CommentDto updatedCommentDto
    ) {
        try {
            CommentEntity comment = commentService.getCommentById(commentId)
                    .orElseThrow(() -> new NoSuchElementException("Comment not found"));
            if (!comment.getUserId().equals(Integer.parseInt(userId))) {
                //권한이 없는 경우 403 Forbidden 상태 코드 반환
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            comment.setContent(updatedCommentDto.getContent());

            CommentEntity updatedComment = commentService.saveComment(comment);
            CommentDto responseCommentDto = new CommentDto(updatedComment);
            responseCommentDto.setEditable(true);
            // ResponseDto와 데이터 설정
            ResponseDto<CommentDto> response = new ResponseDto<>();
            response.setData(Collections.singletonList(responseCommentDto));
            return ResponseEntity.ok().body(response);
        } catch (NoSuchElementException e) {
            // 댓글이 존재하지 않는 경우 404 Not Found 상태 코드 반환
               return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to update the comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}