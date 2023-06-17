package jungle.fairyTeller.board.service;
import com.amazonaws.services.kms.model.NotFoundException;
import jungle.fairyTeller.board.dto.CommentDTO;
import jungle.fairyTeller.board.entity.CommentEntity;
import jungle.fairyTeller.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    public List<CommentDTO> getCommentsByBoardId(Integer boardId) {
        List<CommentEntity> commentEntities = commentRepository.findByBoardBoardId(boardId);
        return commentEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO saveComment(CommentDTO commentDTO) {
        CommentEntity commentEntity = convertToEntity(commentDTO);
        CommentEntity savedComment = commentRepository.save(commentEntity);
        return convertToDTO(savedComment);
    }

    public CommentDTO updateComment(Integer commentId, CommentDTO commentDTO) {
        CommentEntity existingCommentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId));

        existingCommentEntity.setContent(commentDTO.getContent());

        CommentEntity updatedComment = commentRepository.save(existingCommentEntity);
        return convertToDTO(updatedComment);
    }

    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    private CommentDTO convertToDTO(CommentEntity commentEntity) {
        return CommentDTO.builder()
                .commentId(commentEntity.getCommentId())
                .boardId(commentEntity.getBoard().getBoardId())
                .userId(commentEntity.getUserId())
                .nickname(commentEntity.getNickname())
                .content(commentEntity.getContent())
                .createdDatetime(commentEntity.getCreatedDatetime())
                .build();
    }

    private CommentEntity convertToEntity(CommentDTO commentDTO) {
        return CommentEntity.builder()
                .userId(commentDTO.getUserId())
                .nickname(commentDTO.getNickname())
                .content(commentDTO.getContent())
                .build();
    }
}
