package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findByEventAndRequester(long eventId, long userId);

    List<Request> findAllByRequester(long userId);

    List<Request> findAllByEvent(long eventId);

    List<Request> findByIdIn(List<Long> ids);
}
