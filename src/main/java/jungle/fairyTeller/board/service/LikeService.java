package jungle.fairyTeller.board.service;

import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.entity.LikeEntity;
import jungle.fairyTeller.board.repository.BoardRepository;
import jungle.fairyTeller.board.repository.LikeRepository;
import jungle.fairyTeller.user.entity.UserEntity;
import jungle.fairyTeller.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Transactional(readOnly = true)
    public boolean isBoardLiked(Integer boardId, Integer userId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ServiceException("Board not found with id: " + boardId));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with id: " + userId));

        return likeRepository.existsByBoardAndUser(boardEntity, userEntity);
    }

    @Transactional
    public void unlikeBoard(Integer boardId, Integer userId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ServiceException("Board not found with id: " + boardId));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with id: " + userId));

        // Find and delete the like entity for the user and board
        LikeEntity likeEntity = likeRepository.findByBoardAndUser(boardEntity, userEntity)
                .orElseThrow(() -> new ServiceException("Like not found for the user and board"));

        likeRepository.delete(likeEntity);
    }

    @Transactional
    public void likeBoard(Integer boardId, Integer userId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new ServiceException("Board not found with id: " + boardId));

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException("User not found with id: " + userId));

        // Check if the user has already liked the board
        if (isBoardLiked(boardId, userId)) {
            throw new ServiceException("User has already liked the board");
        }

        LikeEntity likeEntity = new LikeEntity();
        likeEntity.setBoard(boardEntity);
        likeEntity.setUser(userEntity);

        likeRepository.save(likeEntity);
    }

}
