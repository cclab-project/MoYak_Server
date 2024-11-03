package study.moyak.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatDTO {

    private String all_image_url;
    private String timeStamp;
    private Long userId;
}
