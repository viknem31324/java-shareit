package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.helpers.Constant;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking addNewBookingRequest(@RequestBody BookingDto bookingDto,
                                           @RequestHeader(name = Constant.HEADER, required = true) long bookerId) {
        return bookingService.addNewBookingRequest(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public Booking approvedBooking(@RequestHeader(name = Constant.HEADER, required = true) long ownerId,
                                      @PathVariable long bookingId,
                                      @RequestParam(required = true) boolean approved) {
        return bookingService.approvedBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingInformation(@RequestHeader(name = Constant.HEADER,
            required = true) long userId, @PathVariable long bookingId) {
        return bookingService.getBookingInformation(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookingListForCurrentUser(@RequestHeader(name = Constant.HEADER,
            required = true) long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingListForCurrentUser(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingListForOwner(@RequestHeader(name = Constant.HEADER, required = true) long userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingListForOwner(userId, state);
    }
}
