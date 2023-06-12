package jungle.fairyTeller.fairyTale.Image.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateImgRequestDTO {
    private String prompt;
    private int height;
    private int width;
    private String format;

    @Builder
    public CreateImgRequestDTO(String prompt, int height, int width, String format) {
        this.prompt = prompt;
        this.height = height;
        this.width = width;
        this.format = format;
    }
}
