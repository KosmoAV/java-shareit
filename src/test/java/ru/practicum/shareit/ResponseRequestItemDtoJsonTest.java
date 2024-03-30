package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ResponseRequestItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ResponseRequestItemDtoJsonTest {

    @Autowired
    private JacksonTester<ResponseRequestItemDto> json;

    @Test
    void tesResponseRequestItemDto() throws Exception {

        ResponseRequestItemDto dto = new ResponseRequestItemDto();
        dto.setId(1L);
        dto.setName("Name");
        dto.setDescription("Description");
        dto.setAvailable(true);
        dto.setRequestId(4L);

        JsonContent<ResponseRequestItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(4);
    }
}
