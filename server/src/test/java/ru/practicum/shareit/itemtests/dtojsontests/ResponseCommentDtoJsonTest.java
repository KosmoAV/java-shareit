package ru.practicum.shareit.itemtests.dtojsontests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ResponseCommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ResponseCommentDtoJsonTest {

    @Autowired
    private JacksonTester<ResponseCommentDto> json;

    @Test
    void testResponseRequestDto() throws Exception {

        ResponseCommentDto dto = new ResponseCommentDto();
        dto.setId(6L);
        dto.setText("Teext");
        dto.setAuthorName("VSE");
        dto.setCreated(LocalDateTime.now());

        JsonContent<ResponseCommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Teext");
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("VSE");
        assertThat(result).extractingJsonPathStringValue("$.created")
                 .isNotNull();
    }
}
