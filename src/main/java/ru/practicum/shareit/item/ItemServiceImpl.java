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

        Item item = itemRepository.addItem(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {

        validUser(itemDto.getOwner());

        Item item = itemRepository.updateItem(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {

        validUser(userId);

        return ItemMapper.toItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public List<ItemDto> getItems(long ownerId) {

        validUser(ownerId);

        return itemRepository.getItems(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {

        validUser(userId);

        return itemRepository.searchItems(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validUser(long userId) {

        if (userRepository.getUser(userId) == null) {
            throw new DataException("User with id = " + userId + " not found");
        }
    }
}
