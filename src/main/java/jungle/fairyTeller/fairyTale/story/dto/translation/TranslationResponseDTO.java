package jungle.fairyTeller.fairyTale.story.dto.translation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class TranslationResponseDTO implements Serializable {
    private MessageDTO message;

    @Builder
    public TranslationResponseDTO(MessageDTO message) {
        this.message = message;
    }
}