package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void addBookingTest() throws Exception {

        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        ItemDto itemDto = makeItemDto(1L, "Vilka", "Description");
        UserDto userDto = makeUserDto(2L, "Alex", "Alex@Alex.ru");
        ResponseBookingDto responseBookingDto = makeResponseBookingDto(1L, now, now.plusDays(1),
                itemDto, userDto, Status.WAITING);

        when(bookingService.addBooking(any()))
                .thenReturn(responseBookingDto);

        CreateBookingDto createBookingDto = makeCreateBookingDto(1L, now, now.plusDays(1), 1L, 2L, null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.booker.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void approveBookingTest() throws Exception {

        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        ItemDto itemDto = makeItemDto(1L, "Vilka", "Description");
        UserDto userDto = makeUserDto(2L, "Alex", "Alex@Alex.ru");
        ResponseBookingDto responseBookingDto = makeResponseBookingDto(1L, now, now.plusDays(1),
                itemDto, userDto, Status.APPROVED);

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseBookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.booker.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void getBookingTest() throws Exception {

        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        ItemDto itemDto = makeItemDto(1L, "Vilka", "Description");
        UserDto userDto = makeUserDto(2L, "Alex", "Alex@Alex.ru");
        ResponseBookingDto responseBookingDto = makeResponseBookingDto(1L, now, now.plusDays(1),
                itemDto, userDto, Status.APPROVED);

        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(responseBookingDto);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.item.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.booker.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.booker.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingTest() throws Exception {

        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        ItemDto itemDto1 = makeItemDto(1L, "Vilka", "Description");
        UserDto userDto1 = makeUserDto(2L, "Alex", "Alex@Alex.ru");
        ResponseBookingDto responseBookingDto1 = makeResponseBookingDto(1L, now, now.plusDays(1),
                itemDto1, userDto1, Status.APPROVED);

        ItemDto itemDto2 = makeItemDto(2L, "Rozetka", "Description32");
        UserDto userDto2 = makeUserDto(3L, "Ivan", "Lola@incest.ru");
        ResponseBookingDto responseBookingDto2 = makeResponseBookingDto(1L, now, now.plusDays(1),
                itemDto2, userDto2, Status.APPROVED);

        when(bookingService.getAllBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBookingDto1, responseBookingDto2));

        mvc.perform(get("/bookings?state=PAST&from=0&size=32")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(responseBookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(notNullValue())))
                .andExpect(jsonPath("$[0].end", is(notNullValue())))
                .andExpect(jsonPath("$[0].item.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].item.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].booker.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(userDto1.getName())))
                .andExpect(jsonPath("$[0].booker.email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$[0].status", is(responseBookingDto1.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(responseBookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(notNullValue())))
                .andExpect(jsonPath("$[1].end", is(notNullValue())))
                .andExpect(jsonPath("$[1].item.id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].item.description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].booker.id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].booker.name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].booker.email", is(userDto2.getEmail())))
                .andExpect(jsonPath("$[1].status", is(responseBookingDto2.getStatus().toString())));
    }

    @Test
    void getAllOwnerBookingTest() throws Exception {

        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        ItemDto itemDto1 = makeItemDto(1L, "Vilka", "Description");
        UserDto userDto1 = makeUserDto(2L, "Alex", "Alex@Alex.ru");
        ResponseBookingDto responseBookingDto1 = makeResponseBookingDto(1L, now, now.plusDays(1),
                itemDto1, userDto1, Status.APPROVED);

        ItemDto itemDto2 = makeItemDto(2L, "Rozetka", "Description32");
        UserDto userDto2 = makeUserDto(3L, "Ivan", "Lola@incest.ru");
        ResponseBookingDto responseBookingDto2 = makeResponseBookingDto(1L, now, now.plusDays(1),
                itemDto2, userDto2, Status.APPROVED);

        when(bookingService.getAllOwnerBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBookingDto1, responseBookingDto2));

        mvc.perform(get("/bookings/owner?state=FUTURE&from=0&size=32")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(responseBookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(notNullValue())))
                .andExpect(jsonPath("$[0].end", is(notNullValue())))
                .andExpect(jsonPath("$[0].item.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].item.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].booker.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(userDto1.getName())))
                .andExpect(jsonPath("$[0].booker.email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$[0].status", is(responseBookingDto1.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(responseBookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(notNullValue())))
                .andExpect(jsonPath("$[1].end", is(notNullValue())))
                .andExpect(jsonPath("$[1].item.id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].item.description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].booker.id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].booker.name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].booker.email", is(userDto2.getEmail())))
                .andExpect(jsonPath("$[1].status", is(responseBookingDto2.getStatus().toString())));
    }

    private CreateBookingDto makeCreateBookingDto(Long id, LocalDateTime start, LocalDateTime end,
                                         Long itemId, Long bookerId, Status status) {

        CreateBookingDto dto = new CreateBookingDto();
        dto.setId(id);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        dto.setBookerId(bookerId);
        dto.setStatus(status);

        return dto;
    }

    private ResponseBookingDto makeResponseBookingDto(Long id, LocalDateTime start, LocalDateTime end,
                                                      ItemDto item, UserDto booker, Status status) {

        ResponseBookingDto dto = new ResponseBookingDto();
        dto.setId(id);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItem(item);
        dto.setBooker(booker);
        dto.setStatus(status);

        return dto;
    }

    private ItemDto makeItemDto(Long id, String name, String description) {

        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);

        return dto;
    }

    private UserDto makeUserDto(Long id, String name, String email) {

        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setEmail(email);
        dto.setName(name);

        return dto;
    }
}