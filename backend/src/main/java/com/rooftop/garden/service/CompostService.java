package com.rooftop.garden.service;

import com.rooftop.garden.entity.CompostBin;
import com.rooftop.garden.entity.CompostDelivery;
import com.rooftop.garden.repository.CompostBinRepository;
import com.rooftop.garden.repository.CompostDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CompostService {

    @Autowired
    private CompostBinRepository binRepository;

    @Autowired
    private CompostDeliveryRepository deliveryRepository;

    public List<CompostBin> getAllBins() {
        return binRepository.findAll();
    }

    public Optional<CompostBin> getBinById(Long id) {
        return binRepository.findById(id);
    }

    public Optional<CompostBin> getBinByCode(String code) {
        return binRepository.findByBinCode(code);
    }

    public List<CompostBin> getBinsByStatus(CompostBin.BinStatus status) {
        return binRepository.findByStatus(status);
    }

    public CompostBin createBin(CompostBin bin) {
        if (bin.getStatus() == null) {
            bin.setStatus(CompostBin.BinStatus.ACTIVE);
        }
        if (bin.getCurrentWeight() == null) {
            bin.setCurrentWeight(BigDecimal.ZERO);
        }
        return binRepository.save(bin);
    }

    public CompostBin updateBin(CompostBin bin) {
        return binRepository.save(bin);
    }

    public List<CompostDelivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Optional<CompostDelivery> getDeliveryById(Long id) {
        return deliveryRepository.findById(id);
    }

    public List<CompostDelivery> getDeliveriesByBinId(Long binId) {
        return deliveryRepository.findByBinIdOrderByDeliveryTimeDesc(binId);
    }

    public List<CompostDelivery> getDeliveriesByUserId(Long userId) {
        return deliveryRepository.findByUserIdOrderByDeliveryTimeDesc(userId);
    }

    public List<CompostDelivery> getUncollectedDeliveries() {
        return deliveryRepository.findByCollectedFalse();
    }

    @Transactional
    public CompostDelivery createDelivery(CompostDelivery delivery) {
        CompostBin bin = binRepository.findById(delivery.getBinId())
                .orElseThrow(() -> new RuntimeException("Compost bin not found"));
        
        BigDecimal newWeight = bin.getCurrentWeight().add(delivery.getWeight());
        bin.setCurrentWeight(newWeight);

        if (newWeight.compareTo(bin.getCapacity()) >= 0) {
            bin.setStatus(CompostBin.BinStatus.FULL);
        } else if (newWeight.compareTo(BigDecimal.ZERO) > 0) {
            bin.setStatus(CompostBin.BinStatus.FILLING);
        }
        binRepository.save(bin);

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public CompostDelivery collectDelivery(Long deliveryId, Long collectorId) {
        CompostDelivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        delivery.setCollected(true);
        delivery.setCollectorId(collectorId);
        delivery.setCollectTime(java.time.LocalDateTime.now());
        
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public CompostBin emptyBin(Long binId) {
        CompostBin bin = binRepository.findById(binId)
                .orElseThrow(() -> new RuntimeException("Compost bin not found"));
        bin.setCurrentWeight(BigDecimal.ZERO);
        bin.setStatus(CompostBin.BinStatus.ACTIVE);
        return binRepository.save(bin);
    }
}
