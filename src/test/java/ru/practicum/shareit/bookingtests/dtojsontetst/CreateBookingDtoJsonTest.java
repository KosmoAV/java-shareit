package ru.practicum.shareit.bookingtests.dtojsontetst;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CreateBookingDtoJsonTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;

    @Test
    void testResponseRequestDto() throws Exception {

        CreateBookingDto dto = new CreateBookingDto();
        dto.setId(6L);
        dto.setItemId(1L);
        dto.setBookerId(6L);
        dto.setStart(LocalDateTime.now().minusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(Status.WAITING);

        JsonContent<CreateBookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(6);
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(Status.WAITING.toString());
    }
}
