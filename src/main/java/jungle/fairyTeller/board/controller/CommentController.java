package jungle.fairyTeller.board.controller;
import jungle.fairyTeller.board.entity.BoardEntity;
import org.hibernate.service.spi.ServiceException;
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
@RequestMapping("/board/{boardId}/comment")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public ResponseEntity<ResponseDto<CommentDto>> getCommentsByBoardIdPaged(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer boardId,
            @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        try {
            // Retrieve pages & comments for specified board with pagination
            Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardId, pageable);
            List<CommentDto> commentDtos = CommentDto.fromEntityList(commentPage.getContent());

            // Set editable value for each comment
            for (CommentDto commentDto : commentDtos) {
                boolean isCommentEditable = commentDto.getUserId().equals(Integer.parseInt(userId));
                commentDto.setEditable(isCommentEditable);
            }

            // Response DTO
            ResponseDto<CommentDto> responseDto = ResponseDto.<CommentDto>builder()
                    .error(null)
                    .data(commentDtos)
                    .currentPage(commentPage.getNumber())
                    .totalPages(commentPage.getTotalPages())
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Failed to retrieve the comments for boardId: {}", boardId, e);
            throw new ServiceException("Failed to retrieve the comments");
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDto<CommentDto>> saveComment(
            @PathVariable Integer boardId, @RequestBody CommentDto commentDto, @AuthenticationPrincipal String userId) {
        try {
            // Retrieve the board entity
            BoardEntity boardEntity = boardService.getBoardById(boardId);

            // Retrieve the user entity
            UserEntity userEntity = userRepository.findById(Integer.parseInt(userId))
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Create the comment entity
            CommentEntity commentEntity = CommentEntity.builder()
                    .content(commentDto.getContent())
                    .board(boardEntity)
                    .user(userEntity)
                    .build();

            // Save the comment entity
            CommentEntity savedCommentEntity = commentService.saveComment(commentEntity);

            // Convert the saved comment to DTO
            CommentDto savedCommentDto = CommentDto.builder()
                    .commentId(savedCommentEntity.getCommentId())
                    .boardId(savedCommentEntity.getBoard().getBoardId())
                    .userId(savedCommentEntity.getUser().getId())
                    .nickname(savedCommentEntity.getUser().getNickname())
                    .content(savedCommentEntity.getContent())
                    .createdDatetime(savedCommentEntity.getCreatedDatetime())
                    .build();
            savedCommentDto.setEditable(savedCommentEntity.getUser().getId().equals(Integer.parseInt(userId)));

            // Response DTO
            ResponseDto<CommentDto> responseDto = ResponseDto.<CommentDto>builder()
                    .error(null)
                    .data(Collections.singletonList(savedCommentDto))
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("Failed to save the comment", e);
            throw new ServiceException("Failed to save the comment");
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDto<CommentDto>> updateComment(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer commentId,
            @RequestBody CommentDto updatedCommentDto
    ) {
        try {
            CommentEntity comment = commentService.getCommentById(commentId)
                    .orElseThrow(() -> new NoSuchElementException("Comment not found"));

            // 댓글 작성자와 현재 사용자의 일치 여부 확인
            boolean isUserCommentAuthor = comment.getUser().getId().equals(Integer.parseInt(userId));
            if(!isUserCommentAuthor){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            comment.setContent(updatedCommentDto.getContent());
            CommentEntity updatedComment = commentService.saveComment(comment);

            CommentDto responseCommentDto = CommentDto.fromEntity(updatedComment);
            responseCommentDto.setEditable(isUserCommentAuthor);

            ResponseDto<CommentDto> response = new ResponseDto<>();
            response.setData(Collections.singletonList(responseCommentDto));
            return ResponseEntity.ok().body(response);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to update the comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(
            @PathVariable Integer boardId,
            @PathVariable Integer commentId,
            @AuthenticationPrincipal String userId
    ) {
        try {
            CommentEntity comment = commentService.getCommentById(commentId)
                    .orElseThrow(() -> new NoSuchElementException("Comment not found"));

            Integer commentUserId = comment.getUser().getId();
            Integer boardAuthorId = boardService.getAuthorByBoardId(boardId).getId();

            if (!commentUserId.equals(Integer.parseInt(userId)) && !boardAuthorId.equals(Integer.parseInt(userId))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            commentService.deleteComment(commentId);

            CommentDto deletedCommentDto = CommentDto.builder()
                    .boardId(boardId)
                    .commentId(commentId)
                    .userId(commentUserId)
                    .nickname(comment.getUser().getNickname())
                    .content(comment.getContent())
                    .editable(false) // 삭제된 댓글은 수정 불가능하도록 설정
                    .createdDatetime(comment.getCreatedDatetime())
                    .build();

            return ResponseEntity.ok(deletedCommentDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to delete the comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
