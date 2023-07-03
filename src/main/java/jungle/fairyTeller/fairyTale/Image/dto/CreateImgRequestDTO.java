package jungle.fairyTeller.fairyTale.Image.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CreateImgRequestDTO {
    public CreateImgRequestDTO(){}
    private int loraNo;
    private String prompt;
    private int height;
    private int width;
    private String format;
    private String img;

    @Builder
    public CreateImgRequestDTO(String prompt, int height, int width, String format, String img, int loraNo) {
        this.prompt = prompt;
        this.height = height;
        this.width = width;
        this.format = format;
        this.img = img;
        this.loraNo = loraNo;
    }
}
