package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.helpers.Constant;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final Logger log = LoggerFactory.getLogger(ItemRequestController.class);

    @PostMapping
    public ResponseEntity<Object> createNewItemRequest(@RequestHeader(name = Constant.HEADER) Long requestorId,
                                                       @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание запроса на вещь: {}", itemRequestDto);
        return itemRequestClient.createNewItemRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsWitchInfo(@RequestHeader(name = Constant.HEADER) Long userId) {
        log.info("Получен запрос на получение информации о запросах для владельца запросов c id: {}", userId);
        return itemRequestClient.getItemRequestsWitchInfo(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsWitchPagination(
            @RequestHeader(name = Constant.HEADER) Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на получение всех запросов начиная с: {}, по: {} запрос", from, size);
        return itemRequestClient.getAllItemRequestsWitchPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getInfoItemRequestById(@RequestHeader(name = Constant.HEADER) Long userId,
                                                         @PathVariable Long requestId) {
        log.info("Получен запрос на получение информации о запросе с id: {}", requestId);
        return itemRequestClient.getInfoItemRequestById(requestId, userId);
    }
}