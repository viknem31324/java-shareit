package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "USERS", schema = "PUBLIC")
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "EMAIL", length = 512, nullable = false, unique = true)
    private String email;
    @Column(name = "NAME", length = 255, nullable = false)
    private String name;
}
