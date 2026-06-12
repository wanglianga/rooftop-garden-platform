package com.rooftop.garden.controller;

import com.rooftop.garden.entity.CompostBin;
import com.rooftop.garden.entity.CompostDelivery;
import com.rooftop.garden.service.CompostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compost")
public class CompostController {

    @Autowired
    private CompostService compostService;

    @GetMapping("/bins")
    public List<CompostBin> getAllBins() {
        return compostService.getAllBins();
    }

    @GetMapping("/bins/{id}")
    public ResponseEntity<CompostBin> getBinById(@PathVariable Long id) {
        return compostService.getBinById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bins/code/{code}")
    public ResponseEntity<CompostBin> getBinByCode(@PathVariable String code) {
        return compostService.getBinByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bins/status/{status}")
    public List<CompostBin> getBinsByStatus(@PathVariable CompostBin.BinStatus status) {
        return compostService.getBinsByStatus(status);
    }

    @PostMapping("/bins")
    public CompostBin createBin(@RequestBody CompostBin bin) {
        return compostService.createBin(bin);
    }

    @PutMapping("/bins/{id}")
    public ResponseEntity<CompostBin> updateBin(@PathVariable Long id, @RequestBody CompostBin bin) {
        return compostService.getBinById(id)
                .map(existing -> {
                    bin.setId(id);
                    return ResponseEntity.ok(compostService.updateBin(bin));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bins/{id}/empty")
    public ResponseEntity<CompostBin> emptyBin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(compostService.emptyBin(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/deliveries")
    public List<CompostDelivery> getAllDeliveries() {
        return compostService.getAllDeliveries();
    }

    @GetMapping("/deliveries/{id}")
    public ResponseEntity<CompostDelivery> getDeliveryById(@PathVariable Long id) {
        return compostService.getDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/deliveries/bin/{binId}")
    public List<CompostDelivery> getDeliveriesByBinId(@PathVariable Long binId) {
        return compostService.getDeliveriesByBinId(binId);
    }

    @GetMapping("/deliveries/user/{userId}")
    public List<CompostDelivery> getDeliveriesByUserId(@PathVariable Long userId) {
        return compostService.getDeliveriesByUserId(userId);
    }

    @GetMapping("/deliveries/uncollected")
    public List<CompostDelivery> getUncollectedDeliveries() {
        return compostService.getUncollectedDeliveries();
    }

    @PostMapping("/deliveries")
    public CompostDelivery createDelivery(@RequestBody CompostDelivery delivery) {
        return compostService.createDelivery(delivery);
    }

    @PostMapping("/deliveries/{id}/collect")
    public ResponseEntity<CompostDelivery> collectDelivery(@PathVariable Long id, @RequestParam Long collectorId) {
        try {
            return ResponseEntity.ok(compostService.collectDelivery(id, collectorId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
