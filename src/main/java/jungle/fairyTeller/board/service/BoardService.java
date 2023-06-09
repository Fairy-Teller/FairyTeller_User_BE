package jungle.fairyTeller.board.service;

import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.repository.BoardRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
