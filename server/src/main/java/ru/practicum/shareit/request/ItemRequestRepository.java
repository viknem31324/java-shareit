package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>  {
    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id = :userId ORDER BY r.created ASC")
    List<ItemRequest> findAllItemRequestsByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id != :userId ORDER BY r.created ASC")
    List<ItemRequest> getAllItemRequestsWitchPagination(Long userId, Pageable pageable);
}
