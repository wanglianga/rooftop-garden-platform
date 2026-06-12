package com.rooftop.garden.service;

import com.rooftop.garden.entity.Plot;
import com.rooftop.garden.entity.PlotClaim;
import com.rooftop.garden.repository.PlotClaimRepository;
import com.rooftop.garden.repository.PlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    @Autowired
    private PlotClaimRepository claimRepository;

    @Autowired
    private PlotRepository plotRepository;

    public List<PlotClaim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Optional<PlotClaim> getClaimById(Long id) {
        return claimRepository.findById(id);
    }

    public List<PlotClaim> getClaimsByUserId(Long userId) {
        return claimRepository.findByUserId(userId);
    }

    public List<PlotClaim> getClaimsByPlotId(Long plotId) {
        return claimRepository.findByPlotId(plotId);
    }

    public List<PlotClaim> getClaimsByStatus(PlotClaim.ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    public List<PlotClaim> getUserActiveClaims(Long userId) {
        return claimRepository.findByUserIdAndStatus(userId, PlotClaim.ClaimStatus.ACTIVE);
    }

    @Transactional
    public PlotClaim createClaim(PlotClaim claim) {
        claim.setStatus(PlotClaim.ClaimStatus.PENDING);
        claim.setClaimDate(LocalDate.now());
        return claimRepository.save(claim);
    }

    @Transactional
    public PlotClaim approveClaim(Long claimId) {
        PlotClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus(PlotClaim.ClaimStatus.ACTIVE);
        claim.setStartDate(LocalDate.now());

        Plot plot = plotRepository.findById(claim.getPlotId())
                .orElseThrow(() -> new RuntimeException("Plot not found"));
        plot.setStatus(Plot.PlotStatus.CLAIMED);
        plot.setCurrentClaimId(claimId);
        plotRepository.save(plot);

        return claimRepository.save(claim);
    }

    @Transactional
    public PlotClaim rejectClaim(Long claimId, String reason) {
        PlotClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setStatus(PlotClaim.ClaimStatus.CANCELLED);
        claim.setRemark(reason);
        return claimRepository.save(claim);
    }

    @Transactional
    public PlotClaim expireClaim(Long claimId) {
        PlotClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setStatus(PlotClaim.ClaimStatus.EXPIRED);
        claim.setEndDate(LocalDate.now());

        Plot plot = plotRepository.findById(claim.getPlotId())
                .orElseThrow(() -> new RuntimeException("Plot not found"));
        plot.setStatus(Plot.PlotStatus.AVAILABLE);
        plot.setCurrentClaimId(null);
        plotRepository.save(plot);

        return claimRepository.save(claim);
    }

    public PlotClaim updateClaim(PlotClaim claim) {
        return claimRepository.save(claim);
    }
}
