package jungle.fairyTeller.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardMainDto<T> {
    private String error;
    private List<T> popularBoards;
    private List<T> data;
    private int currentPage; // 현재 페이지 번호
    private int totalPages; // 총 페이지 수
}
