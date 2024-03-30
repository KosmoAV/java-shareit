package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.CreateRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CreateRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CreateRequestDto> json;

    @Test
    void testCreateRequestDto() throws Exception {
        CreateRequestDto dto = new CreateRequestDto();
        dto.setDescription("Demon vzial myu dushu!!");

        JsonContent<CreateRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Demon vzial myu dushu!!");
    }


}
