package com.rooftop.garden.service;

import com.rooftop.garden.entity.HarvestRecord;
import com.rooftop.garden.entity.Plot;
import com.rooftop.garden.repository.HarvestRecordRepository;
import com.rooftop.garden.repository.PlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HarvestService {

    @Autowired
    private HarvestRecordRepository harvestRepository;

    @Autowired
    private PlotRepository plotRepository;

    public List<HarvestRecord> getAllHarvests() {
        return harvestRepository.findAll();
    }

    public Optional<HarvestRecord> getHarvestById(Long id) {
        return harvestRepository.findById(id);
    }

    public List<HarvestRecord> getHarvestsByPlotId(Long plotId) {
        return harvestRepository.findByPlotIdOrderByHarvestTimeDesc(plotId);
    }

    public List<HarvestRecord> getHarvestsByUserId(Long userId) {
        return harvestRepository.findByUserIdOrderByHarvestTimeDesc(userId);
    }

    public List<HarvestRecord> getHarvestsByPlantingId(Long plantingId) {
        return harvestRepository.findByPlantingIdOrderByHarvestTimeDesc(plantingId);
    }

    public List<HarvestRecord> getHarvestsByDistributionType(HarvestRecord.DistributionType type) {
        return harvestRepository.findByDistributionType(type);
    }

    public HarvestRecord createHarvest(HarvestRecord harvest) {
        return harvestRepository.save(harvest);
    }

    public HarvestRecord updateHarvest(HarvestRecord harvest) {
        return harvestRepository.save(harvest);
    }

    public void deleteHarvest(Long id) {
        harvestRepository.deleteById(id);
    }
}
