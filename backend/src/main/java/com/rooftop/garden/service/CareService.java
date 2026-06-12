package com.rooftop.garden.service;

import com.rooftop.garden.entity.CareLog;
import com.rooftop.garden.repository.CareLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CareService {

    @Autowired
    private CareLogRepository careLogRepository;

    public List<CareLog> getAllCareLogs() {
        return careLogRepository.findAll();
    }

    public Optional<CareLog> getCareLogById(Long id) {
        return careLogRepository.findById(id);
    }

    public List<CareLog> getCareLogsByPlotId(Long plotId) {
        return careLogRepository.findByPlotIdOrderByCareTimeDesc(plotId);
    }

    public List<CareLog> getCareLogsByPlantingId(Long plantingId) {
        return careLogRepository.findByPlantingIdOrderByCareTimeDesc(plantingId);
    }

    public List<CareLog> getCareLogsByUserId(Long userId) {
        return careLogRepository.findByUserIdOrderByCareTimeDesc(userId);
    }

    public List<CareLog> getCareLogsByPlotIdAndType(Long plotId, CareLog.CareType type) {
        return careLogRepository.findByPlotIdAndCareType(plotId, type);
    }

    public CareLog createCareLog(CareLog careLog) {
        return careLogRepository.save(careLog);
    }

    public CareLog updateCareLog(CareLog careLog) {
        return careLogRepository.save(careLog);
    }

    public void deleteCareLog(Long id) {
        careLogRepository.deleteById(id);
    }
}
