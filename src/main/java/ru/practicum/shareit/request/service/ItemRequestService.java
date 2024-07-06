package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createNewItemRequest(Long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponse> getItemRequestsWitchInfo(Long userId);

    List<ItemRequestDtoResponse> getAllItemRequestsWitchPagination(Long userId, int from, int size);

    ItemRequestDtoResponse getInfoItemRequestById(Long requestId, Long userId);

    ItemRequest getItemRequestById(Long requestId);
}
