package jungle.fairyTeller.board.service;
import jungle.fairyTeller.fairyTale.book.entity.BookEntity;
import jungle.fairyTeller.fairyTale.book.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
@RequiredArgsConstructor
@Service
public class BoardService {
    private static final Logger log = LoggerFactory.getLogger(BoardService.class);
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BookRepository bookRepository;
    @Transactional
    public BoardEntity saveBoard(Integer bookId, String userId, String description) {
        // BookEntity 조회
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new ServiceException("Book not found"));

        // 필요한 정보 추출
        String title = bookEntity.getTitle();
        String thumbnailUrl = bookEntity.getThumbnailUrl();

        // BoardEntity 생성
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setTitle(title);
        boardEntity.setThumbnailUrl(thumbnailUrl);
        boardEntity.setDescription(description);
        // BookEntity와 관계 설정
        boardEntity.setBook(bookEntity);
        boardEntity.setPages(bookEntity.getPages());

        // BoardEntity 저장
        return boardRepository.save(boardEntity);
    }


//    public Page<BoardEntity> getAllBoards(Pageable pageable) {
//        try {
//            return boardRepository.findAll(pageable);
//        } catch (Exception e) {
//            log.error("Failed to retrieve boards", e);
//            throw new ServiceException("Failed to retrieve boards");
//        }
//    }
//
//    public Page<BoardEntity> getPagedBoards(Pageable pageable) {
//        return boardRepository.findAll(pageable);
//    }



//
//    @Transactional
//    public BoardEntity saveBoard(BoardEntity boardEntity) {
//        try {
//            boardEntity.
//            return boardRepository.save(boardEntity);
//        } catch (Exception e) {
//            throw new ServiceException("Failed to save the board");
//        }
//    }
//
//    @Transactional(readOnly = true)
//    public BoardEntity getBoardById(Integer boardId) {
//        return boardRepository.findByBoardId(boardId)
//                .orElseThrow(() -> new ServiceException("Board not found with id: " + boardId));
//    }
//
//    @Transactional(readOnly = true)
//    public Integer getAuthorByBoardId(Integer boardId) {
//        BoardEntity boardEntity = getBoardById(boardId);
//        return boardEntity.getAuthor();
//    }
}
