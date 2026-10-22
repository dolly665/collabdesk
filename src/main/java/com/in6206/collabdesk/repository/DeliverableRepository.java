package com.in6206.collabdesk.repository;

import com.in6206.collabdesk.entity.Deliverable;
import com.in6206.collabdesk.entity.DeliverableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {

    List<Deliverable> findByStatusInAndDueDateBefore(Collection<DeliverableStatus> statuses, LocalDate date);
}
