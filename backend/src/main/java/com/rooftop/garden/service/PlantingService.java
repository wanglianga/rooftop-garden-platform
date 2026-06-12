package com.rooftop.garden.service;

import com.rooftop.garden.entity.PlantingRecord;
import com.rooftop.garden.entity.Plot;
import com.rooftop.garden.repository.PlantingRecordRepository;
import com.rooftop.garden.repository.PlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlantingService {

    @Autowired
    private PlantingRecordRepository plantingRepository;

    @Autowired
    private PlotRepository plotRepository;

    public List<PlantingRecord> getAllPlantings() {
        return plantingRepository.findAll();
    }

    public Optional<PlantingRecord> getPlantingById(Long id) {
        return plantingRepository.findById(id);
    }

    public List<PlantingRecord> getPlantingsByPlotId(Long plotId) {
        return plantingRepository.findByPlotId(plotId);
    }

    public List<PlantingRecord> getPlantingsByUserId(Long userId) {
        return plantingRepository.findByUserId(userId);
    }

    public List<PlantingRecord> getPlantingsByStatus(PlantingRecord.PlantingStatus status) {
        return plantingRepository.findByStatus(status);
    }

    @Transactional
    public PlantingRecord createPlanting(PlantingRecord planting) {
        if (planting.getStatus() == null) {
            planting.setStatus(PlantingRecord.PlantingStatus.PLANNED);
        }

        Plot plot = plotRepository.findById(planting.getPlotId())
                .orElseThrow(() -> new RuntimeException("Plot not found"));
        plot.setStatus(Plot.PlotStatus.PLANTING);
        plot.setCurrentCrop(planting.getCropName());
        plotRepository.save(plot);

        return plantingRepository.save(planting);
    }

    public PlantingRecord updatePlanting(PlantingRecord planting) {
        return plantingRepository.save(planting);
    }

    public PlantingRecord updateStatus(Long id, PlantingRecord.PlantingStatus status) {
        return plantingRepository.findById(id)
                .map(planting -> {
                    planting.setStatus(status);
                    return plantingRepository.save(planting);
                })
                .orElseThrow(() -> new RuntimeException("Planting record not found"));
    }

    @Transactional
    public PlantingRecord completePlanting(Long id) {
        PlantingRecord planting = plantingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planting record not found"));
        planting.setStatus(PlantingRecord.PlantingStatus.COMPLETED);

        Plot plot = plotRepository.findById(planting.getPlotId())
                .orElseThrow(() -> new RuntimeException("Plot not found"));
        plot.setStatus(Plot.PlotStatus.IDLE);
        plot.setCurrentCrop(null);
        plotRepository.save(plot);

        return plantingRepository.save(planting);
    }
}
