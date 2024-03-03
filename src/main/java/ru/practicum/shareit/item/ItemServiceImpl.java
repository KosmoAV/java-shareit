package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto) {

        validUser(itemDto.getOwner());

        Item item = itemRepository.save(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {

        validUser(itemDto.getOwner());

        Item newItem = ItemMapper.toItem(itemDto);

        Item item = itemRepository.findById(newItem.getId())
                .orElseThrow(() -> new DataException("Item with id = " + newItem.getId() + " not found"));

        if (newItem.getName() != null) {
            item.setName(newItem.getName());
        }

        if (newItem.getDescription() != null) {
            item.setDescription(newItem.getDescription());
        }

        if (newItem.getAvailable() != null) {
            item.setAvailable(newItem.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {

        validUser(userId);

        return ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new DataException("Item with id = " + itemId + " not found")));
    }

    @Override
    public List<ItemDto> getItems(long ownerId) {

        validUser(ownerId);

        return itemRepository.findByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {

        validUser(userId);

        return itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validUser(long userId) {

        if (!userRepository.existsById(userId)) {
            throw new DataException("User with id = " + userId + " not found");
        }
    }
}
