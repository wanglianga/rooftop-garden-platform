package com.rooftop.garden.repository;

import com.rooftop.garden.entity.GardenSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GardenSuggestionRepository extends JpaRepository<GardenSuggestion, Long> {

    List<GardenSuggestion> findByGardenerIdOrderByCreateTimeDesc(Long gardenerId);

    List<GardenSuggestion> findByPlotIdOrderByCreateTimeDesc(Long plotId);

    List<GardenSuggestion> findByHandledFalse();
}
