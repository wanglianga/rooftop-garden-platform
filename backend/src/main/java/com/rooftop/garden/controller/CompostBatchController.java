package com.rooftop.garden.controller;

import com.rooftop.garden.entity.CompostBatch;
import com.rooftop.garden.service.CompostBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compost-batches")
public class CompostBatchController {

    @Autowired
    private CompostBatchService batchService;

    @GetMapping
    public List<CompostBatch> getAllBatches() {
        return batchService.getAllBatches();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompostBatch> getBatchById(@PathVariable Long id) {
        return batchService.getBatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CompostBatch> getBatchByCode(@PathVariable String code) {
        return batchService.getBatchByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<CompostBatch> getBatchesByStatus(@PathVariable CompostBatch.BatchStatus status) {
        return batchService.getBatchesByStatus(status);
    }

    @PostMapping
    public CompostBatch createBatch(@RequestBody CompostBatch batch) {
        return batchService.createBatch(batch);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompostBatch> updateBatch(@PathVariable Long id, @RequestBody CompostBatch batch) {
        return batchService.getBatchById(id)
                .map(existing -> {
                    batch.setId(id);
                    return ResponseEntity.ok(batchService.updateBatch(batch));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/ferment")
    public ResponseEntity<CompostBatch> startFermentation(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(batchService.startFermentation(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/turn")
    public ResponseEntity<CompostBatch> turnCompost(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(batchService.turnCompost(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/mature")
    public ResponseEntity<CompostBatch> markMature(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(batchService.markMature(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<CompostBatch> applyToPlot(@PathVariable Long id, @RequestParam Long plotId, @RequestParam String destination) {
        try {
            return ResponseEntity.ok(batchService.applyToPlot(id, plotId, destination));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
