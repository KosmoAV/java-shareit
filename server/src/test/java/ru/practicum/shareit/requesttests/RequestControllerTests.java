package ru.practicum.shareit.requesttests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ResponseRequestItemDto;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTests {

    @MockBean
    RequestService requestService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void addRequestTest() throws Exception {

        ResponseRequestDto responseRequestDto = makeResponseRequestDto(1L, "Description", LocalDateTime.now());

        when(requestService.addRequest(anyLong(), any()))
                .thenReturn(responseRequestDto);

        CreateRequestDto createRequestDto = makeCreateRequestDto("Any text");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$.items[0].name", is("Item1")))
                .andExpect(jsonPath("$.items[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$.items[1].name", is("Item2")));
    }

    @Test
    void getUserRequestsTest() throws Exception {

        ResponseRequestDto responseRequestDto1 = makeResponseRequestDto(1L, "Description1", LocalDateTime.now());
        ResponseRequestDto responseRequestDto2 = makeResponseRequestDto(2L, "Description2", LocalDateTime.now());

        when(requestService.getUserRequests(anyLong()))
                .thenReturn(List.of(responseRequestDto1, responseRequestDto2));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(responseRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].created", is(notNullValue())))
                .andExpect(jsonPath("$[0].items", hasSize(2)))
                .andExpect(jsonPath("$[0].items[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is("Item1")))
                .andExpect(jsonPath("$[0].items[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[0].items[1].name", is("Item2")))
                .andExpect(jsonPath("$[1].id", is(responseRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(responseRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(notNullValue())))
                .andExpect(jsonPath("$[1].items", hasSize(2)))
                .andExpect(jsonPath("$[1].items[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[1].items[0].name", is("Item1")))
                .andExpect(jsonPath("$[1].items[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].items[1].name", is("Item2")));
    }

    @Test
    void getAllRequestsTest() throws Exception {

        ResponseRequestDto responseRequestDto1 = makeResponseRequestDto(1L, "Description1", LocalDateTime.now());
        ResponseRequestDto responseRequestDto2 = makeResponseRequestDto(2L, "Description2", LocalDateTime.now());

        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(responseRequestDto1, responseRequestDto2));

        mvc.perform(get("/requests/all?from=0&size=32")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(responseRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(responseRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].created", is(notNullValue())))
                .andExpect(jsonPath("$[0].items", hasSize(2)))
                .andExpect(jsonPath("$[0].items[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is("Item1")))
                .andExpect(jsonPath("$[0].items[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[0].items[1].name", is("Item2")))
                .andExpect(jsonPath("$[1].id", is(responseRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(responseRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(notNullValue())))
                .andExpect(jsonPath("$[1].items", hasSize(2)))
                .andExpect(jsonPath("$[1].items[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[1].items[0].name", is("Item1")))
                .andExpect(jsonPath("$[1].items[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].items[1].name", is("Item2")));
    }

    @Test
    void getRequestByIdTest() throws Exception {

        ResponseRequestDto responseRequestDto = makeResponseRequestDto(1L, "Description", LocalDateTime.now());

        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(responseRequestDto);

        CreateRequestDto createRequestDto = makeCreateRequestDto("Any text");

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$.items[0].name", is("Item1")))
                .andExpect(jsonPath("$.items[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$.items[1].name", is("Item2")));
    }

    private CreateRequestDto makeCreateRequestDto(String description) {

        CreateRequestDto dto = new CreateRequestDto();
        dto.setDescription(description);

        return dto;
    }

    private ResponseRequestDto makeResponseRequestDto(Long id, String description, LocalDateTime created) {

        ResponseRequestDto dto = new ResponseRequestDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setCreated(created);
        dto.setItems(List.of(
                makeResponseRequestItemDto(1L, "Item1"),
                makeResponseRequestItemDto(2L, "Item2")
        ));

        return dto;
    }

    private ResponseRequestItemDto makeResponseRequestItemDto(Long id, String name) {

        ResponseRequestItemDto dto = new ResponseRequestItemDto();
        dto.setId(id);
        dto.setName(name);

        return dto;
    }
}
