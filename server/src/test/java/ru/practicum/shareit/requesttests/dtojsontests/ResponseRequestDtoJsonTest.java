package ru.practicum.shareit.requesttests.dtojsontests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ResponseRequestItemDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ResponseRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ResponseRequestDto> json;

    @Test
    void testResponseRequestDto() throws Exception {

        LocalDateTime time = LocalDateTime.of(2024, 5, 8, 20, 45, 53);

        ResponseRequestItemDto responseRequestItemDto = new ResponseRequestItemDto();
        responseRequestItemDto.setId(2L);
        responseRequestItemDto.setName("pipec");
        responseRequestItemDto.setDescription("Kogda yeto konchitsya!!!");
        responseRequestItemDto.setAvailable(true);
        responseRequestItemDto.setRequestId(4L);

        ResponseRequestDto dto = new ResponseRequestDto();
        dto.setId(6L);
        dto.setDescription("Demon vzial myu dushu!!");
        dto.setCreated(time);
        dto.setItems(List.of(responseRequestItemDto));

        JsonContent<ResponseRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Demon vzial myu dushu!!");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(time.toString());
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("pipec");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("Kogda yeto konchitsya!!!");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(4);
    }
}
