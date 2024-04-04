package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "users", schema = "public")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 16)
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = 32)
    private String email;

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof User)) {
            return false;
        }

        return id != null && id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {

        return id.intValue();
    }
}
