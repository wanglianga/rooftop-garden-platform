package com.rooftop.garden.controller;

import com.rooftop.garden.entity.Plot;
import com.rooftop.garden.service.PlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plots")
public class PlotController {

    @Autowired
    private PlotService plotService;

    @GetMapping
    public List<Plot> getAllPlots() {
        return plotService.getAllPlots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plot> getPlotById(@PathVariable Long id) {
        return plotService.getPlotById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Plot> getPlotByCode(@PathVariable String code) {
        return plotService.getPlotByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<Plot> getPlotsByStatus(@PathVariable Plot.PlotStatus status) {
        return plotService.getPlotsByStatus(status);
    }

    @GetMapping("/available")
    public List<Plot> getAvailablePlots() {
        return plotService.getAvailablePlots();
    }

    @PostMapping
    public Plot createPlot(@RequestBody Plot plot) {
        return plotService.createPlot(plot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plot> updatePlot(@PathVariable Long id, @RequestBody Plot plot) {
        return plotService.getPlotById(id)
                .map(existing -> {
                    plot.setId(id);
                    return ResponseEntity.ok(plotService.updatePlot(plot));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Plot> updatePlotStatus(@PathVariable Long id, @RequestParam Plot.PlotStatus status) {
        try {
            return ResponseEntity.ok(plotService.updatePlotStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlot(@PathVariable Long id) {
        if (plotService.getPlotById(id).isPresent()) {
            plotService.deletePlot(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
