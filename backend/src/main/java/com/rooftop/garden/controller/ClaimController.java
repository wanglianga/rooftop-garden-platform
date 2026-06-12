package com.rooftop.garden.controller;

import com.rooftop.garden.entity.PlotClaim;
import com.rooftop.garden.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @GetMapping
    public List<PlotClaim> getAllClaims() {
        return claimService.getAllClaims();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlotClaim> getClaimById(@PathVariable Long id) {
        return claimService.getClaimById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<PlotClaim> getClaimsByUserId(@PathVariable Long userId) {
        return claimService.getClaimsByUserId(userId);
    }

    @GetMapping("/plot/{plotId}")
    public List<PlotClaim> getClaimsByPlotId(@PathVariable Long plotId) {
        return claimService.getClaimsByPlotId(plotId);
    }

    @GetMapping("/status/{status}")
    public List<PlotClaim> getClaimsByStatus(@PathVariable PlotClaim.ClaimStatus status) {
        return claimService.getClaimsByStatus(status);
    }

    @GetMapping("/user/{userId}/active")
    public List<PlotClaim> getUserActiveClaims(@PathVariable Long userId) {
        return claimService.getUserActiveClaims(userId);
    }

    @PostMapping
    public PlotClaim createClaim(@RequestBody PlotClaim claim) {
        return claimService.createClaim(claim);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PlotClaim> approveClaim(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(claimService.approveClaim(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PlotClaim> rejectClaim(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(claimService.rejectClaim(id, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/expire")
    public ResponseEntity<PlotClaim> expireClaim(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(claimService.expireClaim(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlotClaim> updateClaim(@PathVariable Long id, @RequestBody PlotClaim claim) {
        return claimService.getClaimById(id)
                .map(existing -> {
                    claim.setId(id);
                    return ResponseEntity.ok(claimService.updateClaim(claim));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
