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
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto) {

        validUser(itemDto.getOwner());

        Long requestId = itemDto.getRequestId();
        Item item;

        if (requestId != null) {
            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new DataNotFoundException("Request with id = " + requestId + " not found"));

            item = itemRepository.save(ItemMapper.toItem(itemDto, request));
        } else {
            item = itemRepository.save(ItemMapper.toItem(itemDto, null));
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {

        validUser(itemDto.getOwner());

        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new DataNotFoundException("Item with id = " + itemDto.getId() + " not found"));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
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
        List<ResponseCommentDto> commentsDto = CommentMapper.toResponseCommentDto(comments);

        return ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, commentsDto);
    }

    @Override
    public List<ResponseItemDto> getItems(long ownerId) {

        LocalDateTime now = LocalDateTime.now();

        validUser(ownerId);

        List<Item> items = itemRepository.findByOwnerId(ownerId);

        List<Long> itemsId = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findByItemIdAndStatusOrderByStart(itemsId, Status.APPROVED);
        Map<Long, List<Booking>> bookingListMap = new HashMap<>();

        for (Booking booking : bookings) {

            long itemId = booking.getItem().getId();

            if (bookingListMap.get(itemId) == null) {
                bookingListMap.put(itemId, new ArrayList<>(List.of(booking)));
            } else {
                bookingListMap.get(itemId).add(booking);
            }
        }

        List<Comment> comments = commentRepository.findByItemsId(itemsId);

        return items.stream()
                .map(item -> {

                    List<Comment> commentForItem = comments.stream()
                        .filter(comment -> comment.getItem().getId().equals(item.getId()))
                        .collect(Collectors.toList());

                    List<Booking> bookingList = bookingListMap.get(item.getId());

                    if (bookingList == null) {
                        return ItemMapper.toResponseItemDto(item, null, null,
                                CommentMapper.toResponseCommentDto(commentForItem));
                    }

                    Booking nextBooking = bookingList.stream()
                            .filter(booking -> booking.getStart().isAfter(now))
                            .findFirst().orElse(null);

                    Collections.reverse(bookingList);

                    Booking lastBooking = bookingList.stream()
                            .filter(booking -> booking.getStart().isBefore(now))
                            .findFirst().orElse(null);

                    return ItemMapper.toResponseItemDto(item, lastBooking, nextBooking,
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
