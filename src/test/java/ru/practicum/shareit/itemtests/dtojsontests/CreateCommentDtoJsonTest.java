package ru.practicum.shareit.itemtests.dtojsontests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CreateCommentDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CreateCommentDtoJsonTest {

    @Autowired
    private JacksonTester<CreateCommentDto> json;

    @Test
    void testCreateCommentDto() throws Exception {

        CreateCommentDto dto = new CreateCommentDto();
        dto.setId(1L);
        dto.setText("Texxt");
        dto.setItemId(3L);
        dto.setAuthorId(6L);

        JsonContent<CreateCommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Texxt");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.authorId")
                .isEqualTo(6);
    }
}
