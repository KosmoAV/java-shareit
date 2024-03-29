package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class BookingRepositoryTests {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private User user;
    private Request request;
    private Item item;

    private Booking booking;

    @BeforeEach
    void init() {

        user = new User();
        user.setName("Alex");
        user.setEmail("Kosmo@poza.com");
        testEntityManager.persist(user);

        request = new Request();
        request.setDescription("Description");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(user);
        testEntityManager.persist(request);

        item = new Item();
        item.setName("Pencil");
        item.setDescription("Very small");
        item.setAvailable(true);
        item.setOwnerId(user.getId());
        item.setRequest(request);
        testEntityManager.persist(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        testEntityManager.persist(booking);

        testEntityManager.flush();
    }

    @Test
    void findByBookerIdOrderByStartDescTest() throws Exception {

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByBookerIdWithCurrentStateTest() throws Exception {

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository.findByBookerIdWithCurrentState(user.getId(), page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByBookerIdWithPastStateTest() throws Exception {

        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository.findByBookerIdWithPastState(user.getId(), page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByBookerIdWithFutureStateTest() throws Exception {

        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(15));
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository.findByBookerIdWithFutureState(user.getId(), page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDescApprovedTest() throws Exception {

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.APPROVED, page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDescWaitingTest() throws Exception {

        booking.setStatus(Status.WAITING);
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING, page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.WAITING));
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDescRejectedTest() throws Exception {

        booking.setStatus(Status.REJECTED);
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED, page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.REJECTED));
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDescCanceledTest() throws Exception {

        booking.setStatus(Status.CANCELED);
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.CANCELED, page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.CANCELED));
    }

    @Test
    void findByItemOwnerIdOrderByStartDescTest() throws Exception {

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdOrderByStartDesc(user.getId(), page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByItemOwnerIdWithCurrentStateTest() throws Exception {

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdWithCurrentState(user.getId(), page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByItemOwnerIdWithPastStateTest() throws Exception {

        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdWithPastState(user.getId(), page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByItemOwnerIdWithFutureStateTest() throws Exception {

        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(10));
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdWithFutureState(user.getId(), page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDescApprovedTest() throws Exception {

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(user.getId(), Status.APPROVED, page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDescWaitingTest() throws Exception {

        booking.setStatus(Status.WAITING);
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING, page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.WAITING));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDescRejectedTest() throws Exception {

        booking.setStatus(Status.REJECTED);
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED, page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.REJECTED));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDescCanceledTest() throws Exception {

        booking.setStatus(Status.CANCELED);
        testEntityManager.persistAndFlush(booking);

        PageRequest page = PageRequest.of(0, 32);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(user.getId(), Status.CANCELED, page).toList();
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.CANCELED));
    }

    @Test
    void findLastBookingItemTest() throws Exception {

        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(3));
        testEntityManager.persistAndFlush(booking);

        Booking booking = bookingRepository
                .findLastBookingItem(item.getId(), Status.APPROVED.ordinal());

        assertThat(booking, notNullValue());
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), notNullValue());
        assertThat(booking.getEnd(), notNullValue());
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getBooker(), equalTo(user));
        assertThat(booking.getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void findNextBookingItemTest() throws Exception {

        booking.setStart(LocalDateTime.now().plusDays(1));
        testEntityManager.persistAndFlush(booking);

        Booking booking = bookingRepository
                .findNextBookingItem(item.getId(), Status.APPROVED.ordinal());

        assertThat(booking, notNullValue());
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), notNullValue());
        assertThat(booking.getEnd(), notNullValue());
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getBooker(), equalTo(user));
        assertThat(booking.getStatus(),  equalTo(Status.APPROVED));
    }

    @Test
    void existsByItemIdAndBookerIdAndEndBeforeTest() throws Exception {

        Boolean exist = bookingRepository
                .existsByItemIdAndBookerIdAndEndBefore(item.getId(), user.getId(),
                        LocalDateTime.now().plusDays(3));

        assertThat(exist, equalTo(true));
    }

    @Test
    void findByItemIdAndStatusOrderByStartTest() throws Exception {

        List<Booking> bookingList = bookingRepository
                .findByItemIdAndStatusOrderByStart(List.of(item.getId(), 5L, 9L), Status.APPROVED);
        assertThat(bookingList, notNullValue());
        assertThat(bookingList, hasSize(1));
        assertThat(bookingList.get(0).getId(), notNullValue());
        assertThat(bookingList.get(0).getStart(), notNullValue());
        assertThat(bookingList.get(0).getEnd(), notNullValue());
        assertThat(bookingList.get(0).getItem(), equalTo(item));
        assertThat(bookingList.get(0).getBooker(), equalTo(user));
        assertThat(bookingList.get(0).getStatus(),  equalTo(Status.APPROVED));
    }
}
