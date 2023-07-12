package jungle.fairyTeller.board.controller;

import jungle.fairyTeller.board.dto.*;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.service.BoardService;
import jungle.fairyTeller.board.service.LikeService;
import jungle.fairyTeller.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/board")
public class BestSectionController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    private BoardService boardService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/topBoards")
    public ResponseEntity<BestSectionDto<BoardDto>> getTopBoards(@AuthenticationPrincipal String userId) {
        try {
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

            // Create PopularBoardsDto
            BestSectionDto<BoardDto> popularBoardsDto = BestSectionDto.<BoardDto>builder()
                    .error(null)
                    .data(popularBoardDtos)
                    .build();

            return ResponseEntity.ok(popularBoardsDto);
        } catch (Exception e) {
            log.error("Failed to retrieve the top boards", e);
            throw new ServiceException("Failed to retrieve the top boards");
        }
    }

    @GetMapping("/topAuthors")
    public ResponseEntity<BestSectionDto<AuthorDto>> getTopAuthors(@AuthenticationPrincipal String userId) {
        try {
            List<BoardEntity> allBoards = boardService.getAllBoardsOfTheWeek();

            List<BoardEntity> filteredBoards = allBoards.stream()
                    .filter(board -> board.getHeartCount() >= 1)
                    .collect(Collectors.toList());

            // Calculate heart counts for each author
            Map<UserEntity, Integer> authorHeartCounts = new HashMap<>();
            Map<UserEntity, Date> authorLatestBoardDates = new HashMap<>();
            for (BoardEntity board : filteredBoards) {
                UserEntity author = board.getAuthor();
                int heartCount = authorHeartCounts.getOrDefault(author, 0);
                heartCount += board.getHeartCount();
                authorHeartCounts.put(author, heartCount);

                Date latestBoardDate = authorLatestBoardDates.getOrDefault(author, new Date(0));
                if (board.getCreatedDatetime().after(latestBoardDate)) {
                    authorLatestBoardDates.put(author, board.getCreatedDatetime());
                }
            }

            // Sort authors based on heart counts
            List<UserEntity> popularAuthors = authorHeartCounts.entrySet().stream()
                    .sorted(Map.Entry.<UserEntity, Integer>comparingByValue(Comparator.reverseOrder())
                            .thenComparing(author -> authorLatestBoardDates.get(author.getKey()), Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // Convert popular author entities to DTOs
            List<AuthorDto> popularAuthorDtos = popularAuthors.stream()
                    .limit(3)
                    .filter(authorEntity -> authorHeartCounts.get(authorEntity) > 0)
                    .map(authorEntity -> {
                        // Convert author entity to DTO
                        return AuthorDto.builder()
                                .authorId(authorEntity.getId())
                                .nickname(authorEntity.getNickname())
                                .totalHeart(authorHeartCounts.get(authorEntity))
                                .build();
                    })
                    .collect(Collectors.toList());

            // Create PopularAuthorsDto
            BestSectionDto<AuthorDto> popularAuthorsDto = BestSectionDto.<AuthorDto>builder()
                    .error(null)
                    .data(popularAuthorDtos)
                    .build();

            return ResponseEntity.ok(popularAuthorsDto);
        } catch (Exception e) {
            log.error("Failed to retrieve the top authors", e);
            throw new ServiceException("Failed to retrieve the top authors");
        }
    }

}
