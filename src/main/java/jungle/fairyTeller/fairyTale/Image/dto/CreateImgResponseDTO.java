package jungle.fairyTeller.fairyTale.Image.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CreateImgResponseDTO {
    public CreateImgResponseDTO(){}
    private ArrayList<String> images;
    private CreateImgRequestDTO parameters;
    private String info;
}
