package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {

        if (item == null) {
            throw new IllegalArgumentException("Parameter item in method toItemDto must be non-null");
        }

        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwnerId());
        itemDto.setRequest(item.getRequestId());

        return itemDto;
    }

    public static ResponseItemDto toResponseItemDto(Item item, Booking lastBooking,
                                                    Booking nextBooking, List<ResponseCommentDto> comments) {

        if (item == null) {
            throw new IllegalArgumentException("Parameter item in method toItemDto must be non-null");
        }

        ResponseItemDto responseItemDto = new ResponseItemDto();

        responseItemDto.setId(item.getId());
        responseItemDto.setName(item.getName());
        responseItemDto.setDescription(item.getDescription());
        responseItemDto.setAvailable(item.getAvailable());
        responseItemDto.setOwner(item.getOwnerId());
        responseItemDto.setRequest(item.getRequestId());

        if (lastBooking != null) {
            responseItemDto.setLastBooking(lastBooking.getId(), lastBooking.getBooker().getId());
        }

        if (nextBooking != null) {
            responseItemDto.setNextBooking(nextBooking.getId(), nextBooking.getBooker().getId());
        }

        responseItemDto.setComments(comments);

        return responseItemDto;
    }

    public static Item toItem(ItemDto itemDto) {

        if (itemDto == null) {
            throw new IllegalArgumentException("Parameter itemDto in method toItem must be non-null");
        }

        Item item = new Item();

        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemDto.getOwner());
        item.setRequestId(itemDto.getRequest());

        return item;
    }
}
