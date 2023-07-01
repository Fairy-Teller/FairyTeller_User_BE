package jungle.fairyTeller.fairyTale.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectDTO {
    private String type;
    private Integer left;
    private Integer top;
    private Integer width;
    private Integer height;
    private Integer radius;
}
