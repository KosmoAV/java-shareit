package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.exception.DataBadRequestException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class, ErrorHandler.class})
public class ErrorHandlerTests {

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void illegalArgumentExceptionTest() throws Exception {

        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Message");

        when(itemService.getItems(anyLong()))
                .thenThrow(new IllegalArgumentException("Message"));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(illegalArgumentException.getMessage())))
                .andExpect(jsonPath("$.description", is(illegalArgumentException.getMessage())));
    }

    @Test
    void dataNotFoundExceptionTest() throws Exception {

        DataNotFoundException dataNotFoundException = new DataNotFoundException("Message");

        when(itemService.getItems(anyLong()))
                .thenThrow(new DataNotFoundException("Message"));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Link error")))
                .andExpect(jsonPath("$.description", is(dataNotFoundException.getMessage())));
    }

    @Test
    void dataBadRequestExceptionTest() throws Exception {

        DataBadRequestException dataBadRequestException = new DataBadRequestException("Message");

        when(itemService.getItems(anyLong()))
                .thenThrow(dataBadRequestException);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(dataBadRequestException.getErrorMessage())))
                .andExpect(jsonPath("$.description", is(dataBadRequestException.getMessage())));
    }

    @Test
    void  missingRequestHeaderExceptionTest() throws Exception {

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Missing header parameter")));
    }

    @Test
    void  methodArgumentNotValidExceptionTest() throws Exception {

        ItemDto itemDto = makeItemDto(1L, "", "Description", true, 1L, null);

        when(itemService.addItem(any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")));
    }

    @Test
    void  constraintViolationExceptionTest() throws Exception {

        ItemDto itemDto = makeItemDto(1L, "Name", "Description", true, 1L, null);

        when(itemService.addItem(any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")));
    }

    private ItemDto makeItemDto(Long id, String name, String description,
                                Boolean available, Long owner, Long requestId) {

        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setOwner(owner);
        dto.setRequestId(requestId);

        return dto;
    }
}
