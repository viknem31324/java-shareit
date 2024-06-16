package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>  {
    @Query("SELECT i FROM Item i WHERE i.user.id = :userId")
    List<Item> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) " +
            "LIKE %:text% OR LOWER(i.description) LIKE %:text%) AND i.available = true")
    List<Item> findAllByContainsText(@Param("text") String text);
}
