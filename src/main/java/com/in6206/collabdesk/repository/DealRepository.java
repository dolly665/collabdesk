package com.in6206.collabdesk.repository;

import com.in6206.collabdesk.entity.Deal;
import com.in6206.collabdesk.entity.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findByCreatorIdOrderByUpdatedAtDesc(Long creatorId);

    List<Deal> findByBrandIdOrderByUpdatedAtDesc(Long brandId);

    List<Deal> findAllByOrderByUpdatedAtDesc();

    List<Deal> findByStatusAndPaymentOverdueFalseAndPaymentDueDateBefore(DealStatus status, LocalDate date);

    long countByStatus(DealStatus status);

    @Query("select sum(d.agreedAmount) from Deal d where d.status = com.in6206.collabdesk.entity.DealStatus.PAID")
    BigDecimal sumPaidAmount();
}
