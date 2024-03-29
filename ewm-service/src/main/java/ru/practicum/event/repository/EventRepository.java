package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Event findByInitiatorIdAndId(long userId, long eventId);

    List<Event> findByIdIn(List<Long> ids);

    Event findAllByCategoryId(long catId);

    List<Event> findAllByIdIn(List<Long> ids);

    Optional<Event> findByIdAndState(long id, EventState state);

    List<Event> findByInitiatorIdAndState(long userId, EventState state, Pageable pageable);

    List<Event> findByStateAndInitiatorIdIn(EventState state, List<Long> usersIds, Pageable pageable);
}