package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Table(name = "ITEMS", schema = "PUBLIC")
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private User user;
    @Column(name="NAME", length=255, nullable=false, unique=true)
    private String name;
    @Column(name="DESCRIPTION", length=525, nullable=false)
    private String description;
    @Column(name="AVAILABLE")
    private Boolean available;
//    ItemRequest request;
}
