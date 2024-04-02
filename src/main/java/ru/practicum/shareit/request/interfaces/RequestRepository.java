package ru.practicum.shareit.request.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(value = "SELECT r FROM Request AS r WHERE r.requestor.id = :requestorId ORDER BY r.created DESC")
    public List<Request> findByRequestorId(long requestorId);

    @Query(value = "SELECT r FROM Request AS r WHERE r.requestor.id != :requestorId ORDER BY r.created DESC")
    public List<Request> findByOtherRequestorId(long requestorId);

    @Query(value = "SELECT r FROM Request AS r WHERE r.requestor.id != :requestorId ORDER BY r.created DESC")
    public Page<Request> findByOtherRequestorId(long requestorId, PageRequest page);
}
