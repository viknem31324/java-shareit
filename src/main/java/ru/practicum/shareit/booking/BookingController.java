package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.helpers.Constant;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final Logger log = LoggerFactory.getLogger(BookingController.class);

    @PostMapping
    public Booking addNewBookingRequest(@RequestBody BookingDto bookingDto,
                                        @RequestHeader(name = Constant.HEADER) long bookerId) {
        log.info("Получен запрос на создание нового бронирования: {} для пользователя: {}", bookingDto, bookerId);
        return bookingService.addNewBookingRequest(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public Booking approvedBooking(@RequestHeader(name = Constant.HEADER) long ownerId, @PathVariable long bookingId,
                                   @RequestParam boolean approved) {
        log.info("Получен запрос на подтверждение бронирования: {}, владелец вещи: {}, статус: {}",
                bookingId, ownerId, approved);
        return bookingService.approvedBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingInformation(@RequestHeader(name = Constant.HEADER) long userId,
                                         @PathVariable long bookingId) {
        log.info("Получен запрос на получение информации о бронировании по его id. Бронирование: {}, пользователь: {}",
                bookingId, userId);
        return bookingService.getBookingInformation(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookingListForCurrentUser(@RequestHeader(name = Constant.HEADER) long userId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение информации о бронированиях пользователя по статусу. Пользователь: {}, {}",
                userId, state);
        return bookingService.getBookingListForCurrentUser(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingListForOwner(@RequestHeader(name = Constant.HEADER) long userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение информации о бронированиях владельца по статусу. Пользователь: {}, {}",
                userId, state);
        return bookingService.getBookingListForOwner(userId, state);
    }
}
