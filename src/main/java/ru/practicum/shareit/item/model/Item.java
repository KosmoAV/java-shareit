package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "items", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "description", nullable = false, length = 64)
    private String description;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "owner", nullable = false)
    private Long owner;

    @Column(name = "request")
    private Long request = null;

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Item)) {
            return false;
        }

        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {

        return id.intValue();
    }
}
