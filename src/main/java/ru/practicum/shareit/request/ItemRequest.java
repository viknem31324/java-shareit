package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REQUESTS", schema = "PUBLIC")
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "DESCRIPTION", length = 500, nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REQUESTOR_ID")
    private User requestor;
    @Column(name = "CREATED")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
}
