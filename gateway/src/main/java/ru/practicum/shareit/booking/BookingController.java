package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.helpers.Constant;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final Logger log = LoggerFactory.getLogger(BookingController.class);

    @PostMapping
    public ResponseEntity<Object> addNewBookingRequest(@RequestBody @Valid BookingDto bookingDto,
                                                       @RequestHeader(name = Constant.HEADER) long bookerId) {
        log.info("Получен запрос на создание нового бронирования: {} для пользователя: {}", bookingDto, bookerId);
        return bookingClient.addNewBookingRequest(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader(name = Constant.HEADER) long ownerId,
                                                  @PathVariable long bookingId,
                                                  @RequestParam boolean approved) {
        log.info("Получен запрос на подтверждение бронирования: {}, владелец вещи: {}, статус: {}",
                bookingId, ownerId, approved);
        return bookingClient.approvedBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInformation(@RequestHeader(name = Constant.HEADER) long userId,
                                         @PathVariable long bookingId) {
        log.info("Получен запрос на получение информации о бронировании по его id. Бронирование: {}, пользователь: {}",
                bookingId, userId);
        return bookingClient.getBookingInformation(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingListForCurrentUser(@RequestHeader(name = Constant.HEADER) long userId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на получение информации о бронированиях пользователя по статусу. Пользователь: {}, {}",
                userId, state);
        return bookingClient.getBookingListForCurrentUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingListForOwner(@RequestHeader(name = Constant.HEADER) long userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на получение информации о бронированиях владельца по статусу. Пользователь: {}, {}",
                userId, state);
        return bookingClient.getBookingListForOwner(userId, state, from, size);
    }
}