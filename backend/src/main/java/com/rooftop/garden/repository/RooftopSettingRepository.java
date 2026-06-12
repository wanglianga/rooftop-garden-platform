package com.rooftop.garden.repository;

import com.rooftop.garden.entity.RooftopSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RooftopSettingRepository extends JpaRepository<RooftopSetting, Long> {

    Optional<RooftopSetting> findBySettingKey(String settingKey);
}
