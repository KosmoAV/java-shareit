package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    public ItemDto addItem(ItemDto itemDto);

    public ItemDto updateItem(ItemDto itemDto);

    public ItemDto getItem(long userId, long itemId);

    public List<ItemDto> getItems(long ownerId);

    public List<ItemDto> searchItems(long userId, String text);
}
