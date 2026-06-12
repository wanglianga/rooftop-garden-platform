package com.rooftop.garden.repository;

import com.rooftop.garden.entity.PlotClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlotClaimRepository extends JpaRepository<PlotClaim, Long> {

    List<PlotClaim> findByUserId(Long userId);

    List<PlotClaim> findByPlotId(Long plotId);

    List<PlotClaim> findByStatus(PlotClaim.ClaimStatus status);

    Optional<PlotClaim> findByPlotIdAndStatus(Long plotId, PlotClaim.ClaimStatus status);

    List<PlotClaim> findByUserIdAndStatus(Long userId, PlotClaim.ClaimStatus status);
}
