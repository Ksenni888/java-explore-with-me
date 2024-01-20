package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Event findByInitiatorIdAndId(long userId, long eventId);

    List<Event> findByIdIn(List<Long> ids);

    Event findAllByCategoryId(long catId);

    List<Event> findAllByIdIn(List<Long> ids);
}