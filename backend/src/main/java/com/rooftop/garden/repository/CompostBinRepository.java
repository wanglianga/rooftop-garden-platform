package com.rooftop.garden.repository;

import com.rooftop.garden.entity.CompostBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompostBinRepository extends JpaRepository<CompostBin, Long> {

    Optional<CompostBin> findByBinCode(String binCode);

    List<CompostBin> findByStatus(CompostBin.BinStatus status);
}
