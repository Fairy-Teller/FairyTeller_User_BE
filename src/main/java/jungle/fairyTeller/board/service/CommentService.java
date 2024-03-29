package jungle.fairyTeller.board.service;

import jungle.fairyTeller.board.entity.CommentEntity;
import jungle.fairyTeller.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    public CommentEntity saveComment(CommentEntity comment) {
        return commentRepository.save(comment);
    }

    public Optional<CommentEntity> getCommentById(Integer commentId) {
        return commentRepository.findById(commentId);
    }

    public CommentEntity updateComment(CommentEntity comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);
    }
    public Page<CommentEntity> getCommentsByBoardIdPaged(Integer boardId, Pageable pageable) {
        return commentRepository.findAllByBoardId(boardId, pageable);
    }
}
