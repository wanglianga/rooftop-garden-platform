package com.rooftop.garden.repository;

import com.rooftop.garden.entity.CompostDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompostDeliveryRepository extends JpaRepository<CompostDelivery, Long> {

    List<CompostDelivery> findByBinIdOrderByDeliveryTimeDesc(Long binId);

    List<CompostDelivery> findByUserIdOrderByDeliveryTimeDesc(Long userId);

    List<CompostDelivery> findByCollectedFalse();

    List<CompostDelivery> findByCollectorIdOrderByDeliveryTimeDesc(Long collectorId);
}
