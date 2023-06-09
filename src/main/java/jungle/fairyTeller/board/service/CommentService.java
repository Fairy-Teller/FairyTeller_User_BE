package jungle.fairyTeller.board.service;

import jungle.fairyTeller.board.entity.CommentEntity;
import jungle.fairyTeller.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<CommentEntity> getCommentsByBoardId(Long boardId) {
        return commentRepository.findAllByBoardId(boardId);
    }

    public Optional<CommentEntity> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public CommentEntity updateComment(CommentEntity comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
