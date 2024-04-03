package ru.practicum.shareit.item.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    public List<Item> findByOwnerIdOrderById(Long ownerId);

    public List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);

    @Query(value = "SELECT i FROM Item AS i WHERE i.request.id IN (:requestIds)")
    public List<Item> findByRequestIds(List<Long> requestIds);
}
