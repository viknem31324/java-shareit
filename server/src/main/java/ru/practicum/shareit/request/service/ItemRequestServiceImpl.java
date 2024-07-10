package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final Logger log = LoggerFactory.getLogger(ItemRequestServiceImpl.class);

    @Transactional
    @Override
    public ItemRequestDto createNewItemRequest(Long requestorId, ItemRequestDto itemRequestDto) {
        User requestor = userService.findById(requestorId);
        log.debug("Найден пользователь: {}", requestor);
        ItemRequest itemRequest = ItemRequesMapper.mapToNewItemRequest(itemRequestDto, requestor);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.debug("Создан запрос на вещь: {}", savedItemRequest);
        return ItemRequesMapper.mapToItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDtoResponse> getItemRequestsWitchInfo(Long userId) {
        User user = userService.findById(userId);
        log.debug("Найден пользователь:  {}", user);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllItemRequestsByUserId(userId);
        log.debug("Найден список запросов пользователя с id: {}, запросы: {}", userId, itemRequests);
        Map<Long, List<Item>> itemsMap = getItemsMap(itemRequests);
        List<ItemRequestDtoResponse> resultList = ItemRequesMapper.mapToItemRequestDtoResponse(itemRequests, itemsMap);
        log.debug("Найден список запросов c информацией об ответах: {}", resultList);
        return resultList;
    }

    @Override
    public List<ItemRequestDtoResponse> getAllItemRequestsWitchPagination(Long userId, int from, int size) {
        List<ItemRequest> itemRequests = itemRequestRepository
                .getAllItemRequestsWitchPagination(userId, PageRequest.of(from, size));
        Map<Long, List<Item>> itemsMap = getItemsMap(itemRequests);
        List<ItemRequestDtoResponse> resultList = ItemRequesMapper.mapToItemRequestDtoResponse(itemRequests, itemsMap);
        log.debug("Найден список запросов на вещи с: {}, по: {}. Запросы: {}", from, size, resultList);
        return resultList;
    }

    @Override
    public ItemRequestDtoResponse getInfoItemRequestById(Long requestId, Long userId) {
        User user = userService.findById(userId);
        ItemRequest itemRequest = getItemRequestById(requestId);
        List<Item> items = itemRepository.findItemsByRequestId(requestId);
        List<ItemDto> itemDtos = ItemMapper.mapToItemDto(items);
        ItemRequestDtoResponse itemRequestDtoResponse = new ItemRequestDtoResponse();
        itemRequestDtoResponse.setId(itemRequest.getId());
        itemRequestDtoResponse.setDescription(itemRequest.getDescription());
        itemRequestDtoResponse.setCreated(itemRequest.getCreated());
        itemRequestDtoResponse.setItems(itemDtos);
        log.debug("Запрос с подробной информацией {}", itemRequestDtoResponse);
        return itemRequestDtoResponse;
    }

    @Override
    public ItemRequest getItemRequestById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос на вещь не найден!"));
        log.debug("По id {} найден запрос на вещь: {}", requestId, itemRequest);
        return itemRequest;
    }

    private Map<Long, List<Item>> getItemsMap(List<ItemRequest> itemRequests) {
        List<Long> ids = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<List<Item>> itemsList = itemRepository.findItemsForItemRequestIds(ids);
        Map<Long, List<Item>> itemsMap = new HashMap<>();
        for (List<Item> items : itemsList) {
            if (items.get(0).getRequest() != null) {
                Long requestId = items.get(0).getRequest().getId();
                itemsMap.put(requestId, items);
            }
        }
        return itemsMap;
    }
}
