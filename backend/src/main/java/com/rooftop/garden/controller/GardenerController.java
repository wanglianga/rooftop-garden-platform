package com.rooftop.garden.controller;

import com.rooftop.garden.entity.SoilReport;
import com.rooftop.garden.entity.PestRecord;
import com.rooftop.garden.entity.GardenSuggestion;
import com.rooftop.garden.entity.SeedlingRecord;
import com.rooftop.garden.service.GardenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gardener")
public class GardenerController {

    @Autowired
    private GardenerService gardenerService;

    @GetMapping("/soil-reports")
    public List<SoilReport> getAllSoilReports() {
        return gardenerService.getAllSoilReports();
    }

    @GetMapping("/soil-reports/{id}")
    public ResponseEntity<SoilReport> getSoilReportById(@PathVariable Long id) {
        return gardenerService.getSoilReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/soil-reports/plot/{plotId}")
    public List<SoilReport> getSoilReportsByPlotId(@PathVariable Long plotId) {
        return gardenerService.getSoilReportsByPlotId(plotId);
    }

    @GetMapping("/soil-reports/gardener/{gardenerId}")
    public List<SoilReport> getSoilReportsByGardenerId(@PathVariable Long gardenerId) {
        return gardenerService.getSoilReportsByGardenerId(gardenerId);
    }

    @PostMapping("/soil-reports")
    public SoilReport createSoilReport(@RequestBody SoilReport report) {
        return gardenerService.createSoilReport(report);
    }

    @PutMapping("/soil-reports/{id}")
    public ResponseEntity<SoilReport> updateSoilReport(@PathVariable Long id, @RequestBody SoilReport report) {
        return gardenerService.getSoilReportById(id)
                .map(existing -> {
                    report.setId(id);
                    return ResponseEntity.ok(gardenerService.updateSoilReport(report));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pest-records")
    public List<PestRecord> getAllPestRecords() {
        return gardenerService.getAllPestRecords();
    }

    @GetMapping("/pest-records/{id}")
    public ResponseEntity<PestRecord> getPestRecordById(@PathVariable Long id) {
        return gardenerService.getPestRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pest-records/plot/{plotId}")
    public List<PestRecord> getPestRecordsByPlotId(@PathVariable Long plotId) {
        return gardenerService.getPestRecordsByPlotId(plotId);
    }

    @GetMapping("/pest-records/gardener/{gardenerId}")
    public List<PestRecord> getPestRecordsByGardenerId(@PathVariable Long gardenerId) {
        return gardenerService.getPestRecordsByGardenerId(gardenerId);
    }

    @PostMapping("/pest-records")
    public PestRecord createPestRecord(@RequestBody PestRecord record) {
        return gardenerService.createPestRecord(record);
    }

    @PutMapping("/pest-records/{id}")
    public ResponseEntity<PestRecord> updatePestRecord(@PathVariable Long id, @RequestBody PestRecord record) {
        return gardenerService.getPestRecordById(id)
                .map(existing -> {
                    record.setId(id);
                    return ResponseEntity.ok(gardenerService.updatePestRecord(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/suggestions")
    public List<GardenSuggestion> getAllSuggestions() {
        return gardenerService.getAllSuggestions();
    }

    @GetMapping("/suggestions/{id}")
    public ResponseEntity<GardenSuggestion> getSuggestionById(@PathVariable Long id) {
        return gardenerService.getSuggestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/suggestions/gardener/{gardenerId}")
    public List<GardenSuggestion> getSuggestionsByGardenerId(@PathVariable Long gardenerId) {
        return gardenerService.getSuggestionsByGardenerId(gardenerId);
    }

    @GetMapping("/suggestions/plot/{plotId}")
    public List<GardenSuggestion> getSuggestionsByPlotId(@PathVariable Long plotId) {
        return gardenerService.getSuggestionsByPlotId(plotId);
    }

    @GetMapping("/suggestions/unhandled")
    public List<GardenSuggestion> getUnhandledSuggestions() {
        return gardenerService.getUnhandledSuggestions();
    }

    @PostMapping("/suggestions")
    public GardenSuggestion createSuggestion(@RequestBody GardenSuggestion suggestion) {
        return gardenerService.createSuggestion(suggestion);
    }

    @PostMapping("/suggestions/{id}/handle")
    public ResponseEntity<GardenSuggestion> handleSuggestion(@PathVariable Long id,
                                                              @RequestParam String feedback,
                                                              @RequestParam Long feedbackBy) {
        try {
            return ResponseEntity.ok(gardenerService.handleSuggestion(id, feedback, feedbackBy));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/seedling-records")
    public List<SeedlingRecord> getAllSeedlingRecords() {
        return gardenerService.getAllSeedlingRecords();
    }

    @GetMapping("/seedling-records/{id}")
    public ResponseEntity<SeedlingRecord> getSeedlingRecordById(@PathVariable Long id) {
        return gardenerService.getSeedlingRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/seedling-records/plot/{plotId}")
    public List<SeedlingRecord> getSeedlingRecordsByPlotId(@PathVariable Long plotId) {
        return gardenerService.getSeedlingRecordsByPlotId(plotId);
    }

    @GetMapping("/seedling-records/planting/{plantingId}")
    public List<SeedlingRecord> getSeedlingRecordsByPlantingId(@PathVariable Long plantingId) {
        return gardenerService.getSeedlingRecordsByPlantingId(plantingId);
    }

    @PostMapping("/seedling-records")
    public SeedlingRecord createSeedlingRecord(@RequestBody SeedlingRecord record) {
        return gardenerService.createSeedlingRecord(record);
    }

    @PutMapping("/seedling-records/{id}")
    public ResponseEntity<SeedlingRecord> updateSeedlingRecord(@PathVariable Long id, @RequestBody SeedlingRecord record) {
        return gardenerService.getSeedlingRecordById(id)
                .map(existing -> {
                    record.setId(id);
                    return ResponseEntity.ok(gardenerService.updateSeedlingRecord(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
