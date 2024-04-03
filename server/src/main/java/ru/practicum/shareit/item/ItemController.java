package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                           @RequestBody ItemDto itemDto) {

        log.info("Call 'addItem': ownerId = {}, {}", ownerId, itemDto);

        itemDto.setOwner(ownerId);

        return itemService.addItem(itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId,
                                         @RequestBody CreateCommentDto createCommentDto) {

        log.info("Call 'addComment': userId = {}, {}", userId, createCommentDto);

        createCommentDto.setItemId(itemId);
        createCommentDto.setAuthorId(userId);

        return itemService.addComment(createCommentDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {

        log.info("Call 'updateItem': userId = {}, itemId = {}, {}", ownerId, itemId, itemDto);

        itemDto.setOwner(ownerId);
        itemDto.setId(itemId);

        return itemService.updateItem(itemDto);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long itemId) {

        log.info("Call 'getItem': userId = {}, itemId = {}", userId, itemId);

        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ResponseItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {

        log.info("Call 'getItems': userId = {}", ownerId);

        return itemService.getItems(ownerId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam String text) {

        log.info("Call 'searchItems': userId = {}, text = '{}'", userId, text);

        return itemService.searchItems(userId, text);
    }
}
