package ru.practicum.shareit.item.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    public List<Item> findByOwnerId(Long ownerId);

    public List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);
}
