package com.rooftop.garden.service;

import com.rooftop.garden.entity.SoilReport;
import com.rooftop.garden.entity.PestRecord;
import com.rooftop.garden.entity.GardenSuggestion;
import com.rooftop.garden.entity.SeedlingRecord;
import com.rooftop.garden.repository.SoilReportRepository;
import com.rooftop.garden.repository.PestRecordRepository;
import com.rooftop.garden.repository.GardenSuggestionRepository;
import com.rooftop.garden.repository.SeedlingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GardenerService {

    @Autowired
    private SoilReportRepository soilReportRepository;

    @Autowired
    private PestRecordRepository pestRecordRepository;

    @Autowired
    private GardenSuggestionRepository suggestionRepository;

    @Autowired
    private SeedlingRecordRepository seedlingRepository;

    public List<SoilReport> getAllSoilReports() {
        return soilReportRepository.findAll();
    }

    public Optional<SoilReport> getSoilReportById(Long id) {
        return soilReportRepository.findById(id);
    }

    public List<SoilReport> getSoilReportsByPlotId(Long plotId) {
        return soilReportRepository.findByPlotIdOrderByTestTimeDesc(plotId);
    }

    public List<SoilReport> getSoilReportsByGardenerId(Long gardenerId) {
        return soilReportRepository.findByGardenerIdOrderByTestTimeDesc(gardenerId);
    }

    public SoilReport createSoilReport(SoilReport report) {
        return soilReportRepository.save(report);
    }

    public SoilReport updateSoilReport(SoilReport report) {
        return soilReportRepository.save(report);
    }

    public List<PestRecord> getAllPestRecords() {
        return pestRecordRepository.findAll();
    }

    public Optional<PestRecord> getPestRecordById(Long id) {
        return pestRecordRepository.findById(id);
    }

    public List<PestRecord> getPestRecordsByPlotId(Long plotId) {
        return pestRecordRepository.findByPlotIdOrderByDiscoveryTimeDesc(plotId);
    }

    public List<PestRecord> getPestRecordsByGardenerId(Long gardenerId) {
        return pestRecordRepository.findByGardenerIdOrderByDiscoveryTimeDesc(gardenerId);
    }

    public PestRecord createPestRecord(PestRecord record) {
        return pestRecordRepository.save(record);
    }

    public PestRecord updatePestRecord(PestRecord record) {
        return pestRecordRepository.save(record);
    }

    public List<GardenSuggestion> getAllSuggestions() {
        return suggestionRepository.findAll();
    }

    public Optional<GardenSuggestion> getSuggestionById(Long id) {
        return suggestionRepository.findById(id);
    }

    public List<GardenSuggestion> getSuggestionsByGardenerId(Long gardenerId) {
        return suggestionRepository.findByGardenerIdOrderByCreateTimeDesc(gardenerId);
    }

    public List<GardenSuggestion> getSuggestionsByPlotId(Long plotId) {
        return suggestionRepository.findByPlotIdOrderByCreateTimeDesc(plotId);
    }

    public List<GardenSuggestion> getUnhandledSuggestions() {
        return suggestionRepository.findByHandledFalse();
    }

    public GardenSuggestion createSuggestion(GardenSuggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    public GardenSuggestion handleSuggestion(Long id, String feedback, Long feedbackBy) {
        return suggestionRepository.findById(id)
                .map(s -> {
                    s.setHandled(true);
                    s.setFeedback(feedback);
                    s.setFeedbackBy(feedbackBy);
                    s.setFeedbackTime(java.time.LocalDateTime.now());
                    return suggestionRepository.save(s);
                })
                .orElseThrow(() -> new RuntimeException("Suggestion not found"));
    }

    public List<SeedlingRecord> getAllSeedlingRecords() {
        return seedlingRepository.findAll();
    }

    public Optional<SeedlingRecord> getSeedlingRecordById(Long id) {
        return seedlingRepository.findById(id);
    }

    public List<SeedlingRecord> getSeedlingRecordsByPlotId(Long plotId) {
        return seedlingRepository.findByPlotIdOrderByReplantTimeDesc(plotId);
    }

    public List<SeedlingRecord> getSeedlingRecordsByPlantingId(Long plantingId) {
        return seedlingRepository.findByPlantingIdOrderByReplantTimeDesc(plantingId);
    }

    public SeedlingRecord createSeedlingRecord(SeedlingRecord record) {
        return seedlingRepository.save(record);
    }

    public SeedlingRecord updateSeedlingRecord(SeedlingRecord record) {
        return seedlingRepository.save(record);
    }
}
