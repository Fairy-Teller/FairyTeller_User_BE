package jungle.fairyTeller.board.service;

import jungle.fairyTeller.board.dto.BoardDTO;
import jungle.fairyTeller.board.dto.CommentDTO;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.entity.CommentEntity;
import jungle.fairyTeller.board.repository.BoardRepository;
import jungle.fairyTeller.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<BoardDTO> getAllBoards() {
        List<BoardEntity> boardEntities = boardRepository.findAll();
        return boardEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BoardDTO saveBoard(BoardDTO boardDTO) {
        BoardEntity boardEntity = convertToEntity(boardDTO);
        BoardEntity savedBoard = boardRepository.save(boardEntity);
        return convertToDTO(savedBoard);
    }

    public BoardDTO getBoardById(Integer boardId) {
        BoardEntity boardEntity = boardRepository.findByBoardId(boardId);
        return convertToDTO(boardEntity);
    }

    public String getAuthorByBoardId(Integer boardId) {
        BoardEntity boardEntity = boardRepository.findByBoardId(boardId);
        return boardEntity.getNickname();
    }

    private BoardDTO convertToDTO(BoardEntity boardEntity) {
        return BoardDTO.builder()
                .boardId(boardEntity.getBoardId())
                .bookId(boardEntity.getBookId())
                .authorId(boardEntity.getAuthorId())
                .nickname(boardEntity.getNickname())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .thumbnailUrl(boardEntity.getThumbnailUrl())
                .createdDatetime(boardEntity.getCreatedDatetime())
                .comments(getCommentsByBoardId(boardEntity.getBoardId()))
                .build();
    }

    private BoardEntity convertToEntity(BoardDTO boardDTO) {
        return BoardEntity.builder()
                .bookId(boardDTO.getBookId())
                .authorId(boardDTO.getAuthorId())
                .nickname(boardDTO.getNickname())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .thumbnailUrl(boardDTO.getThumbnailUrl())
                .createdDatetime(boardDTO.getCreatedDatetime())
                .build();
    }

    private List<CommentDTO> getCommentsByBoardId(Integer boardId) {
        List<CommentEntity> commentEntities = commentRepository.findByBoardBoardId(boardId);
        return commentEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
}
