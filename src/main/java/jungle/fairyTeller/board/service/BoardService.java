package jungle.fairyTeller.board.service;
import com.amazonaws.services.kms.model.NotFoundException;
import jungle.fairyTeller.board.repository.CommentRepository;
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

        // Delete the board and its associated comments
        commentRepository.deleteByBoard(boardEntity);
        boardRepository.delete(boardEntity);
    }

}
