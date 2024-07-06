package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.helpers.Constant.BookingStatus;
import ru.practicum.shareit.helpers.Constant.BookingState;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Transactional
    @Override
    public Booking addNewBookingRequest(BookingDto bookingDto, long bookerId) {
        validationBooking(bookingDto);
        User booker = userService.findById(bookerId);
        log.debug("Найден пользователь: {}", booker);
        Item item = itemService.findById(bookingDto.getItemId());
        log.debug("Найдена вещь: {}", item);

        if (item.getUser().getId() == bookerId) {
            throw new ItemNotFoundException("Нельзя взять вещь в аренду у самого себя!");
        }

        List<Booking> bookingCurrent = repository
                .findFirstByItemIdCurrentBooking(item.getId(),
                        bookingDto.getStart(), PageRequest.of(0, 1));

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        if (!bookingCurrent.isEmpty()) {
            return repository.save(booking);
        }

        if (!item.getAvailable()) {
            throw new BookingNotAvailableException("Вещь недоступна для аренды!");
        }
        Booking savedBooking = repository.save(booking);
        log.debug("Создано бронирование: {}", savedBooking);
        return savedBooking;
    }

    @Transactional
    @Override
    public Booking approvedBooking(long ownerId, long bookingId, boolean approved) {
        User user = userService.findById(ownerId);
        log.debug("Найден пользователь: {}", user);
        Booking booking = findBooking(bookingId);
        log.debug("Найдено бронирование: {}", booking);

        long itemId = booking.getItem().getId();
        Item item = itemService.findById(itemId);
        log.debug("Найдена вещь: {}", item);

        if (item.getUser().getId() == ownerId) {
            if (booking.getStatus() == BookingStatus.APPROVED) {
                throw new BookingNotAvailableException("Вещь уже взята в аренду!");
            }

            item.setAvailable(approved);
            itemService.changeItem(item);

            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }

            Booking updatededBooking = repository.save(booking);
            log.debug("Обновленная бронь: {}", updatededBooking);
            return updatededBooking;
        } else {
            throw new NotEnoughRightsException("Пользователь не является владельцем вещи!");
        }
    }

    @Override
    public Booking getBookingInformation(long userId, long bookingId) {
        Booking booking = findBooking(bookingId);
        log.debug("Найдено бронирование: {}", booking);
        User booker = booking.getBooker();
        log.debug("Найден пользователь: {}", booker);
        User owner = booking.getItem().getUser();
        log.debug("Найден владелец: {}", owner);

        if (owner.getId() != userId && booker.getId() != userId) {
            throw new NotEnoughRightsException("Недостаточно прав для просмотра!");
        }

        return booking;
    }

    @Override
    public List<Booking> getBookingListForCurrentUser(long userId, String state, int from, int size) {
        validationStatusBooking(state);

        User user = userService.findById(userId);
        log.debug("Найден пользователь: {}", user);
        int start = from > 0 ? from / size : 0;

        switch (BookingState.valueOf(state)) {
            case ALL:
                return repository.findAllBookingById(userId, PageRequest.of(start, size));
            case WAITING:
                return repository.findAllBookingByIdAndStatus(userId, BookingStatus.WAITING,
                        PageRequest.of(start, size));
            case REJECTED:
                return repository.findAllBookingByIdAndStatus(userId, BookingStatus.REJECTED,
                        PageRequest.of(start, size));
            case PAST:
                return repository.findAllPastBookingById(userId, PageRequest.of(start, size));
            case FUTURE:
                return repository.findAllFutureBookingById(userId, PageRequest.of(start, size));
            case CURRENT:
                return repository.findAllCurrentBookingById(userId, PageRequest.of(start, size));
        }

        return repository.findAllBookingById(userId, PageRequest.of(start, size));
    }

    @Override
    public List<Booking> getBookingListForOwner(long userId, String state, int from, int size) {
        validationStatusBooking(state);

        User user = userService.findById(userId);
        log.debug("Найден пользователь: {}", user);
        int start = from > 0 ? from / size : 0;

        switch (BookingState.valueOf(state)) {
            case ALL:
                return repository.findAllBookingOwnerById(userId, PageRequest.of(start, size));
            case WAITING:
                return repository.findAllBookingOwnerByIdAndStatus(userId, BookingStatus.WAITING,
                        PageRequest.of(start, size));
            case REJECTED:
                return repository.findAllBookingOwnerByIdAndStatus(userId, BookingStatus.REJECTED,
                        PageRequest.of(start, size));
            case PAST:
                return repository.findAllPastOwnerBookingById(userId, PageRequest.of(start, size));
            case FUTURE:
                return repository.findAllFutureOwnerBookingById(userId, PageRequest.of(start, size));
            case CURRENT:
                return repository.findAllCurrentOwnerBookingById(userId, PageRequest.of(start, size));
        }

        return repository.findAllBookingOwnerById(userId, PageRequest.of(start, size));
    }

    private Booking findBooking(long bookingId) {
        Optional<Booking> booking = repository.findById(bookingId);

        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Бронь не найдена!");
        }

        return booking.get();
    }

    private void validationBooking(BookingDto booking) {
        LocalDateTime date = LocalDateTime.now();

        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new ValidationItemException("Не корректные данные бронирования!");
        }

        if (booking.getEnd().isBefore(booking.getStart()) ||
                booking.getStart().isAfter(booking.getEnd()) ||
                booking.getEnd().equals(booking.getStart()) ||
                booking.getStart().isBefore(date)) {
            throw new ValidationItemException("Ошибка в датах бронирования!");
        }
    }

    private void validationStatusBooking(String status) {
        if (!Arrays.toString(BookingState.values()).contains(status)) {
            throw new BookingStateNotFoundException("Unknown state: " + status);
        }
    }
}
