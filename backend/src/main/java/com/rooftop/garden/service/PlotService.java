package com.rooftop.garden.service;

import com.rooftop.garden.entity.Plot;
import com.rooftop.garden.entity.PlotClaim;
import com.rooftop.garden.repository.PlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlotService {

    @Autowired
    private PlotRepository plotRepository;

    public List<Plot> getAllPlots() {
        return plotRepository.findAll();
    }

    public Optional<Plot> getPlotById(Long id) {
        return plotRepository.findById(id);
    }

    public Optional<Plot> getPlotByCode(String code) {
        return plotRepository.findByPlotCode(code);
    }

    public List<Plot> getPlotsByStatus(Plot.PlotStatus status) {
        return plotRepository.findByStatus(status);
    }

    public List<Plot> getAvailablePlots() {
        return plotRepository.findByStatus(Plot.PlotStatus.AVAILABLE);
    }

    public Plot createPlot(Plot plot) {
        if (plot.getStatus() == null) {
            plot.setStatus(Plot.PlotStatus.AVAILABLE);
        }
        return plotRepository.save(plot);
    }

    public Plot updatePlot(Plot plot) {
        return plotRepository.save(plot);
    }

    public void deletePlot(Long id) {
        plotRepository.deleteById(id);
    }

    public Plot updatePlotStatus(Long id, Plot.PlotStatus status) {
        return plotRepository.findById(id)
                .map(plot -> {
                    plot.setStatus(status);
                    return plotRepository.save(plot);
                })
                .orElseThrow(() -> new RuntimeException("Plot not found with id: " + id));
    }
}
