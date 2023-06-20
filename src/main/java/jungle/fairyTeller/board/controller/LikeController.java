package jungle.fairyTeller.board.controller;

import jungle.fairyTeller.board.dto.LikeDto;
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
            //return ResponseEntity.ok("좋아요를 취소했습니다.");
        } else {
            // 좋아요
            likeService.likeBoard(boardId, Integer.parseInt(userId));
            //return ResponseEntity.ok("게시물을 좋아합니다.");
        }
        // 좋아요 횟수와 좋아요 여부를 조회하여 LikeDto 객체로 반환
        int likeCount = likeService.getLikeCount(boardId);
        boolean liked = likeService.isBoardLiked(boardId, Integer.parseInt(userId));
        LikeDto likeDto = new LikeDto(likeCount, liked);
        return ResponseEntity.ok(likeDto);
    }
}
