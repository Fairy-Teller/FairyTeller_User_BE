package jungle.fairyTeller.board.service;
import com.amazonaws.services.kms.model.NotFoundException;
import jungle.fairyTeller.board.entity.LikeEntity;
import jungle.fairyTeller.board.repository.CommentRepository;
import jungle.fairyTeller.board.repository.LikeRepository;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.repository.BookRepository;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.repository.BoardRepository;
import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {
    private static final Logger log = LoggerFactory.getLogger(BoardService.class);
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Transactional
    public BoardEntity saveBoard(Integer bookId, String userId, String description) {
        // BookEntity 조회
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new ServiceException("Book not found"));

        // UserEntity 조회
        UserEntity userEntity = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 필요한 정보 추출
        String title = bookEntity.getTitle();
        String thumbnailUrl = bookEntity.getThumbnailUrl();

        // BoardEntity 생성
        BoardEntity boardEntity = BoardEntity.builder()
                .title(title)
                .thumbnailUrl(thumbnailUrl)
                .description(description)
                .book(bookEntity)
                .author(userEntity)
                .build();

        // BoardEntity 저장
        return boardRepository.save(boardEntity);
    }
    @Transactional(readOnly = true)
    public Page<BoardEntity> getAllBoards(Pageable pageable) {
        try {
            return boardRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Failed to retrieve boards", e);
            throw new ServiceException("Failed to retrieve boards");
        }
    }

    @Transactional(readOnly = true)
    public BoardEntity getBoardById(Integer boardId) {
        return boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ServiceException("Board not found with id: " + boardId));
    }
    @Transactional(readOnly = true)
    public Page<BoardEntity> getAllBoardsPaged(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }
    @Transactional(readOnly = true)
    public UserEntity getAuthorByBoardId(Integer boardId) {
        BoardEntity boardEntity = boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new ServiceException("Board not found with id: " + boardId));

        return boardEntity.getAuthor();
    }

    @Transactional
    public void deleteBoard(Integer boardId) {
        // Retrieve the board entity by boardId
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("Board not found"));

        List<LikeEntity> likes = boardEntity.getLikes();
        likeRepository.deleteAll(likes);

        // Delete the board and its associated comments
        commentRepository.deleteByBoard(boardEntity);
        boardRepository.delete(boardEntity);
    }

    @Transactional(readOnly = true)
    public Page<BoardEntity> searchBoardsByKeyword(String keyword, Pageable pageable) {
        return boardRepository.findByTitleContainingIgnoreCaseOrAuthor_NicknameContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<BoardEntity> searchBoardsByAuthor(String author, Pageable pageable) {
        return boardRepository.findByAuthor_NicknameContainingIgnoreCase(author, pageable);
    }

    @Transactional(readOnly = true)
    public Page<BoardEntity> searchBoardsByTitle(String title, Pageable pageable) {
        return boardRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Transactional
    public void increaseViewCount(Integer boardId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ServiceException("Board not found with id: " + boardId));

        boardEntity.incrementViewCount();
    }
    @Transactional(readOnly = true)
    public List<BoardEntity> getPopularBoardsOfTheWeek(int limit) {
        // Calculate the start and end dates of the current week (from Monday to Sunday)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        log.info("현재 서울 날짜: {}", today);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("현재 서울 날짜와 시간: {}", now);
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 시작일자와 종료일자를 KST로 표현하기 위해 ZoneId를 "Asia/Seoul"로 설정하여 ZonedDateTime으로 변환
        ZonedDateTime startDateTime = startOfWeek.atStartOfDay(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.of("Asia/Seoul"));

        // ZonedDateTime을 LocalDateTime으로 변환
        LocalDateTime startLocalDateTime = startDateTime.toLocalDateTime();
        LocalDateTime endLocalDateTime = endDateTime.toLocalDateTime();

        // LocalDateTime을 Date로 변환
        Date startDate = Date.from(startLocalDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        Date endDate = Date.from(endLocalDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        log.info("getPopularBoardsOfTheWeek 시작일자: {}", startDate);
        log.info("getPopularBoardsOfTheWeek 종료일자: {}", endDate);

        // Retrieve popular boards within the current week based on the length of the likes list and heartCount
        List<BoardEntity> popularBoards = boardRepository.findPopularBoardsByHeartCount(startDate, endDate, limit);

        popularBoards = popularBoards.stream()
                .filter(boardEntity -> likeRepository.countByBoard(boardEntity) > 0)
                .collect(Collectors.toList());

        if (popularBoards.size() > limit) {
            popularBoards = popularBoards.subList(0, limit); // Trim the list to the specified limit
        }
        for (BoardEntity board : popularBoards) {
            Date createdDate = board.getCreatedDatetime();
            Instant instant = createdDate.toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Seoul"));
            log.info("createdDatetime: {}", zonedDateTime);
        }
        return popularBoards;
    }

    public List<BoardEntity> getAllBoardsOfTheWeek() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        log.info("현재 서울 날짜: {}", today);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info("현재 서울 날짜와 시간: {}", now);
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 시작일자와 종료일자를 KST로 표현하기 위해 ZoneId를 "Asia/Seoul"로 설정하여 ZonedDateTime으로 변환
        ZonedDateTime startDateTime = startOfWeek.atStartOfDay(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.of("Asia/Seoul"));

        // ZonedDateTime을 LocalDateTime으로 변환
        LocalDateTime startLocalDateTime = startDateTime.toLocalDateTime();
        LocalDateTime endLocalDateTime = endDateTime.toLocalDateTime();

        // LocalDateTime을 Date로 변환
        Date startDate = Date.from(startLocalDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        Date endDate = Date.from(endLocalDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        log.info("getAllBoardsOfTheWeek 시작일자: {}", startDate);
        log.info("getAllBoardsOfTheWeek 종료일자: {}", endDate);
        return boardRepository.getBoardsBetweenDates(startDate, endDate);
    }
}