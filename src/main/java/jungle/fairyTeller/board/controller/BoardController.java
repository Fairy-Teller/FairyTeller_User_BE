package jungle.fairyTeller.board.controller;
import jungle.fairyTeller.board.service.LikeService;
import jungle.fairyTeller.fairyTale.book.dto.PageDTO;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;
import jungle.fairyTeller.board.dto.BoardMainDto;

@Slf4j
@RestController
@RequestMapping("/board")
public class BoardController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    private BoardService boardService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<BoardMainDto<BoardDto>> getAllBoards(
            @AuthenticationPrincipal String userId,
            @Qualifier("boardPageable") @PageableDefault(size = 8, sort = "boardId", direction = Sort.Direction.DESC) Pageable boardPageable,
            @Qualifier("commentPageable") @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable commentPageable,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String author,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        try {
            // Update pageable objects with dynamic page and size values
            boardPageable = PageRequest.of(page, size, boardPageable.getSort());
            final Pageable finalCommentPageable = PageRequest.of(page, size, commentPageable.getSort());

            // Search boards based on keyword, author, and title
            Page<BoardEntity> searchedBoardPage;

            if (!keyword.isEmpty()) {
                searchedBoardPage = boardService.searchBoardsByKeyword(keyword, boardPageable);
            } else if (!author.isEmpty()) {
                searchedBoardPage = boardService.searchBoardsByAuthor(author, boardPageable);
            } else if (!title.isEmpty()) {
                searchedBoardPage = boardService.searchBoardsByTitle(title, boardPageable);
            } else {
                // If no search parameters are provided, retrieve all boards
                searchedBoardPage = boardService.getAllBoards(boardPageable);
            }

            // Convert board entities to DTOs
            List<BoardDto> boardDtos = searchedBoardPage.getContent().stream()
                    .map(boardEntity -> {
                        // Retrieve pages for each board
                        List<PageDTO> pageDTOs = PageDTO.fromEntityList(boardEntity.getBook().getPages());

                        // Retrieve comments for each board with pagination
                        Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardEntity.getBoardId(), finalCommentPageable);

                        List<CommentDto> commentDtos = CommentDto.fromEntityList(commentPage.getContent());

                        int likeCount = likeService.getLikeCount(boardEntity.getBoardId());
                        boolean liked = likeService.isBoardLiked(boardEntity.getBoardId(), Integer.parseInt(userId));

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
                                .likeCount(likeCount)
                                .liked(liked)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Fetch popular boards of the week
            List<BoardEntity> popularBoards = boardService.getPopularBoardsOfTheWeek(3); // Change the limit as needed

            // Convert popular board entities to DTOs
            List<BoardDto> popularBoardDtos = popularBoards.stream()
                    .map(boardEntity -> {
                        // Convert board entity to DTO
                        return BoardDto.builder()
                                .boardId(boardEntity.getBoardId())
                                .bookId(boardEntity.getBook().getBookId())
                                .title(boardEntity.getTitle())
                                .thumbnailUrl(boardEntity.getThumbnailUrl())
                                .createdDatetime(boardEntity.getCreatedDatetime())
                                .authorId(boardEntity.getAuthor().getId())
                                .nickname(boardEntity.getAuthor().getNickname())
                                .likeCount(likeService.getLikeCount(boardEntity.getBoardId()))
                                .liked(likeService.isBoardLiked(boardEntity.getBoardId(), Integer.parseInt(userId)))
                                .build();
                    })
                    .collect(Collectors.toList());

            // Response DTO
            BoardMainDto<BoardDto> responseDto = BoardMainDto.<BoardDto>builder()
                    .error(null)
                    .popularBoards(popularBoardDtos)
                    .data(boardDtos)
                    .currentPage(searchedBoardPage.getNumber())
                    .totalPages(searchedBoardPage.getTotalPages())
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Failed to retrieve the boards", e);
            throw new ServiceException("Failed to retrieve the boards");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<BoardMainDto<BoardDto>> saveBoard(
            @AuthenticationPrincipal String userId,
            @RequestBody BoardDto requestDto,
            @Qualifier("boardPageable") @PageableDefault(size = 8, sort = "boardId", direction = Sort.Direction.DESC) Pageable boardPageable,
            @Qualifier("commentPageable") @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable commentPageable,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        try {
            boardPageable = PageRequest.of(page, size, boardPageable.getSort());
            final Pageable finalCommentPageable = PageRequest.of(page, size, commentPageable.getSort());

            BoardEntity savedBoardEntity = boardService.saveBoard(requestDto.getBookId(), userId, requestDto.getDescription());

            // Fetch popular boards of the week
            List<BoardEntity> popularBoards = boardService.getPopularBoardsOfTheWeek(3); // Change the limit as needed

            // Convert popular board entities to DTOs
            List<BoardDto> popularBoardDtos = popularBoards.stream()
                    .map(boardEntity -> {
                        // Convert board entity to DTO
                        return BoardDto.builder()
                                .boardId(boardEntity.getBoardId())
                                .bookId(boardEntity.getBook().getBookId())
                                .title(boardEntity.getTitle())
                                .thumbnailUrl(boardEntity.getThumbnailUrl())
                                .createdDatetime(boardEntity.getCreatedDatetime())
                                .authorId(boardEntity.getAuthor().getId())
                                .nickname(boardEntity.getAuthor().getNickname())
                                .likeCount(likeService.getLikeCount(boardEntity.getBoardId()))
                                .liked(likeService.isBoardLiked(boardEntity.getBoardId(), Integer.parseInt(userId)))
                                .build();
                    })
                    .collect(Collectors.toList());

            // Fetch sorted board page
            Page<BoardEntity> sortedBoardPage = boardService.getAllBoardsPaged(boardPageable);
            List<BoardDto> boardDtos = sortedBoardPage.getContent().stream()
                    .map(boardEntity -> {
                        // Retrieve pages for each board
                        List<PageDTO> pageDTOs = PageDTO.fromEntityList(boardEntity.getBook().getPages());

                        // Retrieve comments for each board with pagination
                        Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardEntity.getBoardId(), commentPageable);
                        List<CommentDto> commentDtos = CommentDto.fromEntityList(commentPage.getContent());

                        int likeCount = likeService.getLikeCount(boardEntity.getBoardId());
                        boolean liked = likeService.isBoardLiked(boardEntity.getBoardId(), Integer.parseInt(userId));

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
                                .likeCount(likeCount)
                                .liked(liked)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Response DTO
            BoardMainDto<BoardDto> responseDto = new BoardMainDto<>();
            responseDto.setPopularBoards(popularBoardDtos);
            responseDto.setData(boardDtos);
            responseDto.setCurrentPage(sortedBoardPage.getNumber());
            responseDto.setTotalPages(sortedBoardPage.getTotalPages());

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Failed to save the board", e);
            throw new ServiceException("Failed to save the board");
        }
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<BoardDto>> getBoardById(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer boardId,
            @PageableDefault(size = 10, sort = "commentId", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            // Retrieve the board entity by boardId
            BoardEntity boardEntity = boardService.getBoardById(boardId);
            boolean isEditable = boardEntity.getAuthor().getId().equals(Integer.parseInt(userId));

            viewCountUp(boardId, request, response);

            // Retrieve pages for the board
            List<PageDTO> pageDTOs = PageDTO.fromEntityList(boardEntity.getBook().getPages());

            // Retrieve comments for the board with pagination
            Page<CommentEntity> commentPage = commentService.getCommentsByBoardIdPaged(boardId, pageable);
            List<CommentDto> commentDtos = CommentDto.fromEntityList(commentPage.getContent());

            int likeCount = likeService.getLikeCount(boardEntity.getBoardId());
            boolean liked = likeService.isBoardLiked(boardEntity.getBoardId(), Integer.parseInt(userId));

            //Set editable value for each comment
            for (CommentDto commentDto : commentDtos) {
                boolean isCommentEditable = commentDto.getUserId().equals(Integer.parseInt(userId));
                commentDto.setEditable(isCommentEditable);
            }

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
                    .comments(commentDtos)
                    .editable(isEditable)
                    .pages(pageDTOs)
                    .likeCount(likeCount)
                    .liked(liked)
                    .viewCount(boardEntity.getViewCount())
                    .build();

            // Response DTO
            ResponseDto<BoardDto> responseDto = ResponseDto.<BoardDto>builder()
                    .error(null)
                    .data(Collections.singletonList(boardDto))
                    .currentPage(commentPage.getNumber())
                    .totalPages(commentPage.getTotalPages())
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("Failed to retrieve the board", e);
            throw new ServiceException("Failed to retrieve the board");
        }
    }

    private void viewCountUp(Integer id, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Board id: {}", id);
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.info("Cookie Name: {}, Cookie Value: {}", cookie.getName(), cookie.getValue());
                if (cookie.getName().equals("viewedBoards")) {
                    oldCookie = cookie;
                }
            }
        } else {
            logger.info("No cookies found in the request");
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
                boardService.increaseViewCount(id);
                oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            } else {
                logger.info("It's already been viewed");
            }
        } else {
            boardService.increaseViewCount(id);
            Cookie newCookie = new Cookie("viewedBoards","[" + id + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBoard(
            @AuthenticationPrincipal String userId,
            @PathVariable Integer boardId
    ) {
        try {
            BoardEntity boardEntity = boardService.getBoardById(boardId);
            boolean isEditable = boardEntity.getAuthor().getId().equals(Integer.parseInt(userId));
            if (!isEditable) {
                throw new IllegalArgumentException("You are not authorized to delete this board.");
            }
            // Delete the board and its associated comments
            boardService.deleteBoard(boardId);
            // Return a success message
            return ResponseEntity.ok("Board and associated comments deleted successfully.");
        } catch (Exception e) {
            log.error("Failed to delete the board", e);
            throw new ServiceException("Failed to delete the board");
        }
    }
}
