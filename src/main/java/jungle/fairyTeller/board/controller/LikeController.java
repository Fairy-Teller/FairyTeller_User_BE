package jungle.fairyTeller.board.controller;

import jungle.fairyTeller.board.dto.LikeDto;
import jungle.fairyTeller.board.entity.BoardEntity;
import jungle.fairyTeller.board.repository.BoardRepository;
import jungle.fairyTeller.board.service.BoardService;
import jungle.fairyTeller.board.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    @PostMapping("/{boardId}/like")
    public ResponseEntity<LikeDto> likeBoard(
            @PathVariable Integer boardId,
            @AuthenticationPrincipal String userId
    ) {

        // 게시물에 대한 좋아요 확인
        boolean isLiked = likeService.isBoardLiked(boardId, Integer.parseInt(userId));

        if (isLiked) {
            // 좋아요를 취소
            likeService.unlikeBoard(boardId, Integer.parseInt(userId));
        } else {
            // 좋아요
            likeService.likeBoard(boardId, Integer.parseInt(userId));
        }
        // 좋아요 횟수와 좋아요 여부를 조회하여 LikeDto 객체로 반환
        BoardEntity boardEntity = boardService.getBoardById(boardId);
        int likeCount = likeService.getLikeCount(boardEntity.getBoardId());
        boardEntity.setHeartCount(likeCount);
        boardRepository.save(boardEntity);
        boolean liked = likeService.isBoardLiked(boardId, Integer.parseInt(userId));
        LikeDto likeDto = new LikeDto(likeCount, liked);
        return ResponseEntity.ok(likeDto);
    }
}
