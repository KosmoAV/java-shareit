package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.DataBadRequestException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
//import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

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
                .orElseThrow(() -> new DataNotFoundException("Item with id = " + newItem.getId() + " not found"));

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
    public ResponseItemDto getItem(long userId, long itemId) {

        validUser(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item with id = " + itemId + " not found"));

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwnerId() == userId) {
            lastBooking = bookingRepository.findLastBookingItem(itemId, Status.APPROVED.ordinal());
            nextBooking = bookingRepository.findNextBookingItem(itemId, Status.APPROVED.ordinal());
        }

        List<Comment> comments = commentRepository.findByItemId(itemId);

        //List<ResponseCommentDto> commentDto = CommentMapper.toResponseCommentDto(comments);

        return new ResponseItemDto();
                //ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, CommentMapper.toResponseCommentDto(comments));

    }

    @Override
    public List<ResponseItemDto> getItems(long ownerId) {

        validUser(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);

        List<Long> itemsId = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findByItemsId(itemsId);

        return items.stream()
                .map(item -> {

                    List<Comment> commentForItem = comments.stream()
                        .filter(comment -> comment.getItem().getId().equals(item.getId()))
                        .collect(Collectors.toList());

                    return ItemMapper.toResponseItemDto(item,
                    bookingRepository.findLastBookingItem(item.getId(), Status.APPROVED.ordinal()),
                    bookingRepository.findNextBookingItem(item.getId(), Status.APPROVED.ordinal()),
                    CommentMapper.toResponseCommentDto(commentForItem));

                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {

        validUser(userId);

        return itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseCommentDto addComment(CreateCommentDto createCommentDto) {

        User author = userRepository.findById(createCommentDto.getAuthorId())
                .orElseThrow(() -> new DataNotFoundException("User with id = " + createCommentDto.getAuthorId() + " not found"));

        Item item = itemRepository.findById(createCommentDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Item with id = " + createCommentDto.getItemId() + " not found"));

        boolean isBooking = bookingRepository
                .existsByItemIdAndBookerIdAndEndBefore(item.getId(), author.getId(), LocalDateTime.now());

        if (isBooking) {
            Comment comment = commentRepository.save(CommentMapper.toComment(createCommentDto, item, author));
            return CommentMapper.toResponseCommentDto(comment);
        }

        throw new DataBadRequestException("User with id = " + author.getId()
                + " do not booking item with id = " + item.getId());
    }

    private void validUser(long userId) {

        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException("User with id = " + userId + " not found");
        }
    }
}
