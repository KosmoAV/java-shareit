package ru.practicum.shareit.itemtests.dtojsontests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ResponseItemDtoJsonTest {

    @Autowired
    private JacksonTester<ResponseItemDto> json;

    @Test
    void testResponseItemDto() throws Exception {

        ResponseCommentDto responseCommentDto = new ResponseCommentDto();
        responseCommentDto.setId(3L);
        responseCommentDto.setText("KAK YA USTAL!");
        responseCommentDto.setAuthorName("Programmist");
        responseCommentDto.setCreated(LocalDateTime.now());

        ResponseItemDto dto = new ResponseItemDto();
        dto.setId(1L);
        dto.setName("Name");
        dto.setDescription("Description");
        dto.setAvailable(true);
        dto.setOwner(2L);
        dto.setRequest(4L);
        dto.setLastBooking(1L, 2L);
        dto.setNextBooking(2L, 5L);
        dto.setComments(List.of(responseCommentDto));

        JsonContent<ResponseItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Name");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner")
                .isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.request")
                .isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(5);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo("KAK YA USTAL!");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("Programmist");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isNotNull();
    }
}
