package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.OnPatch;
import ru.practicum.shareit.group.OnPost;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import java.util.ArrayList;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                          @RequestBody @Validated(OnPost.class) ItemDto itemDto) {

        log.info("Call 'addItem': ownerId = {}, {}", ownerId, itemDto);

        itemDto.setOwner(ownerId);

        return itemClient.addItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                         @PathVariable @Positive long itemId,
                                         @RequestBody @Validated CreateCommentDto createCommentDto) {

        log.info("Call 'addComment': userId = {}, {}", userId, createCommentDto);

        createCommentDto.setItemId(itemId);
        createCommentDto.setAuthorId(userId);

        return itemClient.addComment(userId, itemId, createCommentDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                              @PathVariable @Positive long itemId,
                              @RequestBody @Validated(OnPatch.class) ItemDto itemDto) {

        log.info("Call 'updateItem': userId = {}, itemId = {}, {}", ownerId, itemId, itemDto);

        itemDto.setOwner(ownerId);
        itemDto.setId(itemId);

        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                   @PathVariable @Positive long itemId) {

        log.info("Call 'getItem': userId = {}, itemId = {}", userId, itemId);

        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId) {

        log.info("Call 'getItems': userId = {}", ownerId);

        return itemClient.getItems(ownerId);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                     @RequestParam(required = true) String text) {

        log.info("Call 'searchItems': userId = {}, text = '{}'", userId, text);

        if (text.isBlank()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        return itemClient.searchItems(userId, text);
    }
}
