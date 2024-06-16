package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final UserServiceImpl userService;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        validationItem(itemDto);
        User owner = userService.findById(ownerId);
        Item item = ItemMapper.mapToNewItem(itemDto, owner);
        Item savedItem = repository.save(item);

        return ItemMapper.mapToItemDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        Item findItem = findById(itemId);
        User owner = userService.findById(ownerId);

        if (!Objects.equals(findItem.getUser().getId(), owner.getId())) {
            throw new NotEnoughRightsException("Пользователь не является владельцем вещи!");
        }

        Item item = createdValidItem(itemDto, findItem, itemId, owner);
        Item savedItem = repository.save(item);
        return ItemMapper.mapToItemDto(savedItem);
    }

    @Override
    public ItemDtoBooking findItemDtoById(Long itemId, Long ownerId) {
        Item item = findById(itemId);

        return getItemDtoBooking(ownerId, item);
    }

    @Override
    public List<ItemDtoBooking> findAllItemsForUser(Long ownerId) {
        List<Item> items = repository.findAllByUserId(ownerId);

        return items.stream().map(item -> getItemDtoBooking(ownerId, item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> users = repository.findAllByContainsText(text.toLowerCase());

        return ItemMapper.mapToItemDto(users);
    }

    @Transactional
    @Override
    public void changeItem(Item item) {
        repository.save(item);
    }

    @Override
    public Item findById(final Long itemId) {
        Optional<Item> itemOpt = repository.findById(itemId);

        if (itemOpt.isEmpty()) {
            throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена!");
        }

        return itemOpt.get();
    }

    @Override
    public CommentDto addComment(CommentDtoRequest commentDtoRequest, Long itemId, Long authorId) {
        Item findItem = findById(itemId);
        User author = userService.findById(authorId);
        List<Booking> booking = bookingRepository.findBookingByIdUserAndIdItem(authorId, itemId);

        if (commentDtoRequest.getText().isBlank()) {
            throw new CommentValidationException("Текст отзыва не может быть пустым!");
        }

        if (booking.isEmpty()) {
            throw new CommentValidationException("Оставить отзыв может только тот кто брал ее в аренду!");
        }

        Comment comment = CommentMapper.mapToNewComment(commentDtoRequest, findItem, author);
        Comment saveComment = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(saveComment);
    }

    private ItemDtoBooking getItemDtoBooking(Long ownerId, Item item) {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentDtos = Collections.emptyList();
        if (!comments.isEmpty()) {
            commentDtos = CommentMapper.mapToCommentDto(comments);
        }

        List<Booking> bookingCurrent = bookingRepository
                .findFirstByItemIdCurrentBooking(item.getId(),
                        LocalDateTime.now(), PageRequest.of(0, 1));

        Item currentItem = item;
        if (bookingCurrent.isEmpty()) {
            currentItem = new Item();
            currentItem.setId(item.getId());
            currentItem.setUser(item.getUser());
            currentItem.setName(item.getName());
            currentItem.setDescription(item.getDescription());
            currentItem.setAvailable(true);
            repository.save(currentItem);
        }
        BookingDtoResponse last = null;
        BookingDtoResponse next =  null;
        if (Objects.equals(ownerId, item.getUser().getId())) {
            List<Booking> bookingLast = bookingRepository
                    .findFirstByItemIdLastBooking(item.getId(), BookingStatus.APPROVED,
                            LocalDateTime.now(), PageRequest.of(0, 1));
            if (!bookingLast.isEmpty()) {
                last = BookingMapper.mapToBookingDtoResponse(bookingLast.get(0));
            }
            List<Booking> bookingNext = bookingRepository
                    .findFirstByItemIdNextBooking(item.getId(), BookingStatus.APPROVED,
                            LocalDateTime.now(), PageRequest.of(0, 1));
            if (!bookingNext.isEmpty()) {
                next = BookingMapper.mapToBookingDtoResponse(bookingNext.get(0));
            }
        }
        return ItemMapper.mapToItemDtoBooking(currentItem, last, next, commentDtos);
    }

    private void validationItem(ItemDto item) {
        if (item.getAvailable() == null || item.getName() == null ||
                item.getDescription() == null || item.getName().isBlank()) {
            throw new ValidationItemException("Некорректные данные вещи!");
        }
    }

    private Item createdValidItem(ItemDto itemDto, Item item, Long itemId, User owner) {
        Item newItem = new Item();
        newItem.setId(itemId);
        newItem.setUser(owner);
        newItem.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        newItem.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        newItem.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());
        return newItem;
    }
}
