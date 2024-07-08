package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.exception.ItemNotFoundException;
import ru.practicum.shareit.error.exception.NotEnoughRightsException;
import ru.practicum.shareit.error.exception.ValidationItemException;
import ru.practicum.shareit.helpers.Constant;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = new ItemDto(1L, "ItemName", "ItemDescription",
            true, null);
    private final ItemDtoBooking itemDtoBooking = new ItemDtoBooking(1L, "ItemName", "ItemDescription",
            true, null, null, null);

    @Test
    public void findItemById_whenItemFound_thenStatusOkAndMockDtoEqualsResponseDto() throws Exception {
        when(itemService.findItemDtoById(1L, 1L))
                .thenReturn(itemDtoBooking);

        mvc.perform(get("/items/{itemId}", itemDtoBooking.getId())
                        .header(Constant.HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoBooking.getAvailable())));
    }

    @Test
    public void findItemById_whenItemNotFound_thenStatusNotFoundAndThrowsItemNotFoundException() throws Exception {
        when(itemService.findItemDtoById(99L, 1L))
                .thenThrow(new ItemNotFoundException("Вещь с id 99 не найдена!"));

        mvc.perform(get("/items/{itemId}", 99)
                        .header(Constant.HEADER, 1L))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(ItemNotFoundException.class));
    }

    @Test
    public void addItem_whenCreateItem_thenStatusCreatedAndMockDtoEqualsResponseDto() throws Exception {
        when(itemService.addItem(itemDto, 1L))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(Constant.HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    public void addItem_whenCreateItemWitchIncorrectData_shouldThrowsException() throws Exception {
        ItemDto newItemDto = new ItemDto(1L, "", null,
                null, null);
        when(itemService.addItem(newItemDto, 1L))
                .thenThrow(new ValidationItemException("Некорректные данные вещи!"));

        mvc.perform(post("/items")
                        .header(Constant.HEADER, 1L)
                        .content(mapper.writeValueAsString(newItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(ValidationItemException.class));
    }

    @Test
    public void updateItem_whenUpdateItem_thenStatusOkAndMockDtoEqualsResponseDto() throws Exception {
        ItemDto updateItemDto = new ItemDto(1L, "UpdateItemName", "ItemDescription",
                true, null);

        when(itemService.updateItem(updateItemDto, 1L, 1L))
                .thenReturn(updateItemDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(Constant.HEADER, 1L)
                        .content(mapper.writeValueAsString(updateItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updateItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updateItemDto.getAvailable())));
    }

    @Test
    public void updateItem_whenUpdateItemIfOwnerIdItIsNotOwner_thenStatusOkAndMockDtoEqualsResponseDto()
            throws Exception {
        when(itemService.updateItem(itemDto, 1L, 99L))
                .thenThrow(new NotEnoughRightsException("Пользователь не является владельцем вещи!"));

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(Constant.HEADER, 99L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass()
                        .equals(NotEnoughRightsException.class));
    }

    @Test
    public void findAllItemsDtoForUser_whenInvoked_shouldReturnCollectionWitchItemsDto() throws Exception {
        when(itemService.findAllItemsForUser(1L, 0, 10))
                .thenReturn(List.of(itemDtoBooking));

        mvc.perform(get("/items")
                        .header(Constant.HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoBooking.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoBooking.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoBooking.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoBooking.getAvailable())));
    }

    @Test
    public void searchItems_whenInvoked_shouldReturnCollectionWitchItemsDto() throws Exception {
        when(itemService.searchItems("ItemName", 0, 10))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "ItemName")
                        .header(Constant.HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    public void addComment_whenAddComment_thenStatusOkAndMockDtoEqualsResponseDto() throws Exception {
        CommentDtoRequest newComment = new CommentDtoRequest("text");
        CommentDto commentDto = new CommentDto(1L, "text", "authorName", LocalDateTime.now());

        when(itemService.addComment(newComment, 1L, 1L))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(Constant.HEADER, 1L)
                        .content(mapper.writeValueAsString(newComment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}
