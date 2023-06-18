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
import org.springframework.beans.factory.annotation.Qualifier;
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
    public ResponseEntity<ResponseDto<BoardDto>> getAllBoards(
            @AuthenticationPrincipal String userId,
            @Qualifier("boardPageable") @PageableDefault(size = 8, sort = "boardId", direction = Sort.Direction.DESC) Pageable boardPageable,
            @Qualifier("commentPageable") @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable commentPageable,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        try {
            // Update pageable objects with dynamic page and size values
            boardPageable = PageRequest.of(page, size, boardPageable.getSort());
            final Pageable finalCommentPageable = PageRequest.of(page, size, commentPageable.getSort());

            // Retrieve sorted board page
            Page<BoardEntity> sortedBoardPage = boardService.getAllBoards(boardPageable);

            // Convert board entities to DTOs
            List<BoardDto> boardDtos = sortedBoardPage.getContent().stream()
                    .map(boardEntity -> {
                        // Retrieve pages for each board
                        List<PageDTO> pageDTOs = PageDTO.fromEntityList(boardEntity.getBook().getPages());

                        // Retrieve comments for each board with pagination
                        Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardEntity.getBoardId(), finalCommentPageable);

                        List<CommentDto> commentDtos = CommentDto.fromEntityList(commentPage.getContent());

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
                                .pages(pageDTOs != null ? pageDTOs : new ArrayList<>())
                                .comments(commentDtos != null ? commentDtos : new ArrayList<>())
                                .build();
                    })
                    .collect(Collectors.toList());

            // Response DTO
            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
                    .error(null)
                    .data(boardDtos)
                    .currentPage(sortedBoardPage.getNumber())
                    .totalPages(sortedBoardPage.getTotalPages())
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Failed to retrieve the boards", e);
            throw new ServiceException("Failed to retrieve the boards");
        }
    }

//    @PostMapping("/save")
//    public ResponseEntity<ResponseDto<BoardDto>> saveBoard(
//            @AuthenticationPrincipal String userId,
//            @RequestBody BoardDto requestDto
//    ) {
//        try {
//            BoardEntity boardEntity = boardService.saveBoard(requestDto.getBookId(), userId, requestDto.getDescription());
//            Pageable pageable = PageRequest.of(0, 9); // 페이지 크기와 정렬 방식을 지정
//            ResponseDto<BoardDto> response = getAllBoardsResponse(pageable); // 수정된 부분
//
//            List<BoardDto> boardDtos = sortedBoardPage.getContent().stream()
//                    .map(boardEntity -> {
//                        // Retrieve pages for each board
//                        List<PageDTO> pageDTOs = PageDTO.fromEntityList(boardEntity.getBook().getPages());
//
//                        // Retrieve comments for each board with pagination
//                        Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardEntity.getBoardId(), commentPageable);
//                        List<CommentDto> commentDtos = CommentDto.fromEntityList(commentPage.getContent());
//
//                        // Convert board entity to DTO
//                        return BoardDto.builder()
//                                .boardId(boardEntity.getBoardId())
//                                .bookId(boardEntity.getBook().getBookId())
//                                .title(boardEntity.getTitle())
//                                .description(boardEntity.getDescription())
//                                .thumbnailUrl(boardEntity.getThumbnailUrl())
//                                .createdDatetime(boardEntity.getCreatedDatetime())
//                                .authorId(boardEntity.getAuthor().getId())
//                                .nickname(boardEntity.getAuthor().getNickname())
//                                .pages(pageDTOs != null ? pageDTOs : new ArrayList<>())
//                                .comments(commentDtos != null ? commentDtos : new ArrayList<>())
//                                .build();
//                    })
//                    .collect(Collectors.toList());
//
//            // Response DTO
//            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
//                    .error(null)
//                    .data(boardDtos)
//                    .build();
//
//            return ResponseEntity.ok(responseDto);
//        } catch (Exception e) {
//            log.error("Failed to save the board", e);
//            throw new ServiceException("Failed to save the board");
//        }
//    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardDto>> getBoardById(@AuthenticationPrincipal String userId, @PathVariable Integer boardId) {
        try {
            // Retrieve the board entity by boardId
            BoardEntity boardEntity = boardService.getBoardById(boardId);
            boolean isEditable = boardEntity.getAuthor().getId().equals(Integer.parseInt(userId));

            // Retrieve pages for the board
            List<PageDTO> pageDTOs = PageDTO.fromEntityList(boardEntity.getBook().getPages());

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
                    .editable(isEditable)
                    .pages(pageDTOs)
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

}
