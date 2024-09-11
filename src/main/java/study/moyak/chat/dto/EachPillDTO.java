package study.moyak.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EachPillDTO {

    private String image;
    private String pill_name;
    private String pill_ingredient;
}
