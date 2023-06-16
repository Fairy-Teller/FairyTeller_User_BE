package jungle.fairyTeller.fairyTale.story.dto.translation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class MessageDTO implements Serializable {
    private ResultDTO result;

    @Builder
    public MessageDTO(ResultDTO result) {
        this.result = result;
    }
}

