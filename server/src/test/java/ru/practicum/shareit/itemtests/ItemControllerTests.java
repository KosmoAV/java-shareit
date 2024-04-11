package ru.practicum.shareit.itemtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void addItemTest() throws Exception {

        ItemDto itemDto = makeItemDto(1L, "Name", "Description", true, 1L, null);

        when(itemService.addItem(any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner().intValue())))
                .andExpect(jsonPath("$.requestId", is(nullValue())));
    }

    @Test
    void addCommentTest() throws Exception {

        ResponseCommentDto responseCommentDto = makeResponseCommentDto(1L, "Text", "Alex");

        when(itemService.addComment(any()))
                .thenReturn(responseCommentDto);

        CreateCommentDto createCommentDto = makeCreateCommentDto(1L, "Text", 1L, 1L);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(createCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(responseCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(responseCommentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }

    @Test
    void updateItemTest() throws Exception {

        ItemDto itemDto = makeItemDto(1L, "Name", "Description", true, 1L, null);

        when(itemService.updateItem(any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner().intValue())))
                .andExpect(jsonPath("$.requestId", is(nullValue())));
    }

    @Test
    void getItemTest() throws Exception {

        ResponseItemDto responseItemDto = makeResponseItemDto(1L, "Name", "Alex", 2L, 4L);

        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(responseItemDto);

        mvc.perform(get("/items/1")
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseItemDto.getName())))
                .andExpect(jsonPath("$.description", is(responseItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(responseItemDto.getAvailable())))
                .andExpect(jsonPath("$.owner", is(responseItemDto.getOwner().intValue())))
                .andExpect(jsonPath("$.request", is(responseItemDto.getRequest().intValue())))
                .andExpect(jsonPath("$.lastBooking", is(notNullValue())))
                .andExpect(jsonPath("$.nextBooking", is(notNullValue())))
                .andExpect(jsonPath("$.comments", is(notNullValue())));
    }

    @Test
    void getItemsTest() throws Exception {

        ResponseItemDto responseItemDto1 = makeResponseItemDto(1L, "Name", "Alex", 2L, 4L);
        ResponseItemDto responseItemDto2 = makeResponseItemDto(2L, "Kosmo", "Ivan", 4L, 8L);

        when(itemService.getItems(anyLong()))
                .thenReturn(List.of(responseItemDto1, responseItemDto2));

        mvc.perform(get("/items")
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(responseItemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(responseItemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(responseItemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(responseItemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(responseItemDto1.getOwner().intValue())))
                .andExpect(jsonPath("$[0].request", is(responseItemDto1.getRequest().intValue())))
                .andExpect(jsonPath("$[1].id", is(responseItemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(responseItemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(responseItemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(responseItemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].owner", is(responseItemDto2.getOwner().intValue())))
                .andExpect(jsonPath("$[1].request", is(responseItemDto2.getRequest().intValue())));
    }

    @Test
    void searchItemsTest() throws Exception {

        ItemDto itemDto1 = makeItemDto(1L, "Name", "Description", true, 1L, null);
        ItemDto itemDto2 = makeItemDto(2L, "Ilia", "Text", true, 3L, null);

        when(itemService.searchItems(anyLong(), anyString()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mvc.perform(get("/items/search?text=iva")
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(itemDto1.getOwner().intValue())))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].owner", is(itemDto2.getOwner().intValue())));
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

    private CreateCommentDto makeCreateCommentDto(Long id, String text, Long itemId, Long authorId) {

        CreateCommentDto dto = new CreateCommentDto();
        dto.setId(id);
        dto.setText(text);
        dto.setItemId(itemId);
        dto.setAuthorId(authorId);

        return dto;
    }

    private ResponseCommentDto makeResponseCommentDto(Long id, String text, String authorName) {

        ResponseCommentDto dto = new ResponseCommentDto();
        dto.setId(id);
        dto.setText(text);
        dto.setAuthorName(authorName);
        dto.setCreated(LocalDateTime.now());

        return dto;
    }

    private ResponseItemDto makeResponseItemDto(Long id, String name, String description,
                                                Long owner, Long request) {

        ResponseItemDto dto = new ResponseItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setOwner(owner);
        dto.setRequest(request);

        dto.setAvailable(true);
        dto.setLastBooking(1, 2);
        dto.setNextBooking(3, 4);

        dto.setComments(new ArrayList<>());

        return dto;
    }
}
