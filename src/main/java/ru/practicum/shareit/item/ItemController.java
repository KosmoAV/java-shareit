package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemService;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                           @RequestBody ItemDto itemDto) {

        log.info("Call 'addItem': ownerId = {}, {}", ownerId, itemDto);

        validateId(ownerId);
        validate(itemDto, true);

        itemDto.setOwner(ownerId);

        return itemService.addItem(itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {

        log.info("Call 'updateItem': userId = {}, itemId = {}, {}", ownerId, itemId, itemDto);

        validateId(ownerId);
        validateId(itemId);
        validate(itemDto, false);

        itemDto.setOwner(ownerId);
        itemDto.setId(itemId);

        return itemService.updateItem(itemDto);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {

        log.info("Call 'getItem': userId = {}, itemId = {}", userId, itemId);

        validateId(userId);
        validateId(itemId);

        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {

        log.info("Call 'getItems': userId = {}", ownerId);

        return itemService.getItems(ownerId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(required = true) String text) {

        log.info("Call 'searchItems': userId = {}, text = '{}'", userId, text);

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemService.searchItems(userId, text);
    }

    private void validate(ItemDto itemDto, boolean allFieldRequired) throws ValidationException {

        if (allFieldRequired) {

            validateName(itemDto.getName());
            validateDescription(itemDto.getDescription());
            validateAvailable(itemDto.getAvailable());

        } else {

            if (itemDto.getName() == null && itemDto.getDescription() == null && itemDto.getAvailable() == null) {
                throw new ValidationException("At least one field of item must be non-null");
            }

            if (itemDto.getName() != null) {
                validateName(itemDto.getName());
            }

            if (itemDto.getDescription() != null) {
                validateDescription(itemDto.getDescription());
            }

            if (itemDto.getAvailable() != null) {
                validateAvailable(itemDto.getAvailable());
            }
        }
    }

    private void validateId(Long id) throws ValidationException {
        if (id < 1) {
            throw new ValidationException("Incorrect id '" + id + "'");
        }
    }

    private void validateName(String name) throws ValidationException {

        if (name == null || name.isBlank()) {
            throw new ValidationException("Incorrect item name '" + name + "'");
        }
    }

    public void validateDescription(String description) throws ValidationException {

        if (description == null || description.isBlank()) {

            throw new ValidationException("Incorrect item description '" + description + "'");
        }
    }

    public void validateAvailable(Boolean available) throws ValidationException {

        if (available == null) {

            throw new ValidationException("Incorrect item available 'null'");
        }
    }
}
