package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    public Item addItem(Item item);

    public Item updateItem(Item item);

    public Item getItem(long itemId);

    public List<Item> getItems(long ownerId);

    public List<Item> searchItems(String text);
}
