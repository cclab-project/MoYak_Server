package study.moyak.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {

    private String allImage;
    private String title;
    private List<EachPillDTO> eachPills;

    public ChatDTO(List<EachPillDTO> eachPills) {
        this.eachPills = eachPills;
    }
}
