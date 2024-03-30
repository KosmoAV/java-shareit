package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(3L);
        dto.setName("Alex");
        dto.setEmail("Pipec@BigPipec.com");

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Alex");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Pipec@BigPipec.com");
    }


}
