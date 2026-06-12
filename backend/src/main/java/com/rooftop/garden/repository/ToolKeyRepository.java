package com.rooftop.garden.repository;

import com.rooftop.garden.entity.ToolKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolKeyRepository extends JpaRepository<ToolKey, Long> {

    Optional<ToolKey> findByKeyCode(String keyCode);

    List<ToolKey> findByStatus(ToolKey.KeyStatus status);

    List<ToolKey> findByCurrentBorrowerId(Long borrowerId);
}
