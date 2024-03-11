package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

public interface ItemService {

    public ItemDto addItem(ItemDto itemDto);

    public ItemDto updateItem(ItemDto itemDto);

    public ResponseItemDto getItem(long userId, long itemId);

    public List<ResponseItemDto> getItems(long ownerId);

    public List<ItemDto> searchItems(long userId, String text);

    public ResponseCommentDto addComment(CreateCommentDto createCommentDto);
}
