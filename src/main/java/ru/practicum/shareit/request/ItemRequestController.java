package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.helpers.Constant;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final Logger log = LoggerFactory.getLogger(ItemRequestController.class);

    @PostMapping
    public ItemRequestDto createNewItemRequest(@RequestHeader(name = Constant.HEADER) Long requestorId,
                                               @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание запроса на вещь: {}", itemRequestDto);
        return itemRequestService.createNewItemRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getItemRequestsWitchInfo(@RequestHeader(name = Constant.HEADER) Long userId) {
        log.info("Получен запрос на получение информации о запросах для владельца запросов c id: {}", userId);
        return itemRequestService.getItemRequestsWitchInfo(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAllItemRequestsWitchPagination(
            @RequestHeader(name = Constant.HEADER) Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на получение всех запросов начиная с: {}, по: {} запрос", from, size);
        return itemRequestService.getAllItemRequestsWitchPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getInfoItemRequestById(@RequestHeader(name = Constant.HEADER) Long userId,
                                                         @PathVariable Long requestId) {
        log.info("Получен запрос на получение информации о запросе с id: {}", requestId);
        return itemRequestService.getInfoItemRequestById(requestId, userId);
    }
}
