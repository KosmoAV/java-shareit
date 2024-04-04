package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private Long request = null;

    private InnerBookingDto lastBooking = null;

    private InnerBookingDto nextBooking = null;

    private List<ResponseCommentDto> comments;

    public void setLastBooking(long id, long bookerId) {
        lastBooking = new InnerBookingDto();
        lastBooking.setId(id);
        lastBooking.setBookerId(bookerId);
    }

    public void setNextBooking(long id, long bookerId) {
        nextBooking = new InnerBookingDto();
        nextBooking.setId(id);
        nextBooking.setBookerId(bookerId);
    }

    @Data
    private class InnerBookingDto {
        private long id;
        private long bookerId;
    }
}


