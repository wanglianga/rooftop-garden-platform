package com.rooftop.garden.repository;

import com.rooftop.garden.entity.VisitorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorRecordRepository extends JpaRepository<VisitorRecord, Long> {

    List<VisitorRecord> findByStatus(VisitorRecord.VisitStatus status);

    List<VisitorRecord> findByInvitedByIdOrderByCreateTimeDesc(Long invitedById);
}
