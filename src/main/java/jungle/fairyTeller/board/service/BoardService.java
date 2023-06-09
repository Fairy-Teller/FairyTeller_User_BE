package jungle.fairyTeller.board.service;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@RequiredArgsConstructor
@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    public List<BoardEntity> getAllBoards() {
        try {
            return boardRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve boards");
        }
    }
    @Transactional
    public BoardEntity saveBoard(BoardEntity boardEntity) {
        try {
            return boardRepository.save(boardEntity);
        } catch (Exception e) {
            throw new ServiceException("Failed to save the board");
        }
    }
}
