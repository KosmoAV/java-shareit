package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private long id = 0;
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {

        item.setId(getId());

        if (items.containsKey(item.getOwner())) {
            items.get(item.getOwner()).put(item.getId(), item);
        } else {
            Map<Long, Item> itemMap = new HashMap<>();
            itemMap.put(item.getId(), item);
            items.put(item.getOwner(), itemMap);
        }

        return item;
    }

    @Override
    public Item updateItem(Item item) {

        validItemNotRegistered(item.getOwner(), item.getId());

        Item saveItem = items.get(item.getOwner()).get(item.getId());

        if (item.getName() == null) {
            item.setName(saveItem.getName());
        }

        if (item.getDescription() == null) {
            item.setDescription(saveItem.getDescription());
        }

        if (item.getAvailable() == null) {
            item.setAvailable(saveItem.getAvailable());
        }

        items.get(item.getOwner()).put(item.getId(), item);

        return item;
    }

    @Override
    public Item getItem(long itemId) {

        for (Map<Long, Item> itemMap : items.values()) {
            if (itemMap.containsKey(itemId)) {
                return itemMap.get(itemId);
            }
        }

        throw new DataException("Item id = " + itemId + " does not exist");
    }

    @Override
    public List<Item> getItems(long ownerId) {

        validItemsNotRegistration(ownerId);

        return new ArrayList<>(items.get(ownerId).values());
    }

    @Override
    public List<Item> searchItems(String text) {

        return items.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                        && item.getAvailable())
                .collect(Collectors.toList());
    }

    private long getId() {
        return ++id;
    }

    private void validItemNotRegistered(long ownerId, long itemId) throws DataException {

        validItemsNotRegistration(ownerId);

        if (items.get(ownerId).get(itemId) == null) {
            throw new DataException("Item id = " + itemId + " for owner id = " + ownerId + " does not exist");
        }
    }

    private void validItemsNotRegistration(long ownerId) {

        Map<Long, Item> itemMap= items.get(ownerId);

        if (itemMap == null) {
            throw new DataException("Items for owner id = " + ownerId + " does not exist");
        }
    }
}
