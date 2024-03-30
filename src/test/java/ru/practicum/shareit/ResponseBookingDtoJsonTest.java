package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ResponseBookingDtoJsonTest {

    @Autowired
    private JacksonTester<ResponseBookingDto> json;

    @Test
    void testResponseRequestDto() throws Exception {

        ResponseBookingDto dto = new ResponseBookingDto();
        dto.setId(6L);
        dto.setItem(new ItemDto());
        dto.setBooker(new UserDto());
        dto.setStart(LocalDateTime.now().minusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(Status.WAITING);

        JsonContent<ResponseBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(6);
        assertThat(result).extractingJsonPathValue("$.item")
                .isNotNull();
        assertThat(result).extractingJsonPathValue("$.booker")
                .isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(Status.WAITING.toString());
    }
}
