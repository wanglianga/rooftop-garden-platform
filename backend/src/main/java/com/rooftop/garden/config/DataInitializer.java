package com.rooftop.garden.config;

import com.rooftop.garden.entity.*;
import com.rooftop.garden.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private PlotClaimRepository claimRepository;

    @Autowired
    private CompostBinRepository binRepository;

    @Autowired
    private CompostBatchRepository batchRepository;

    @Autowired
    private PlantingRecordRepository plantingRepository;

    @Autowired
    private HarvestRecordRepository harvestRepository;

    @Autowired
    private ToolKeyRepository toolKeyRepository;

    @Autowired
    private RooftopSettingRepository settingRepository;

    @Override
    public void run(String... args) {
        initUsers();
        initPlots();
        initCompostBins();
        initCompostBatches();
        initToolKeys();
        initSettings();
        initSampleClaimsAndPlantings();
    }

    private void initUsers() {
        if (userRepository.count() > 0) return;

        User resident1 = new User();
        resident1.setUsername("resident1");
        resident1.setName("张小明");
        resident1.setRole(User.Role.RESIDENT);
        resident1.setPhone("13800138001");
        resident1.setAddress("3号楼2单元501");
        userRepository.save(resident1);

        User resident2 = new User();
        resident2.setUsername("resident2");
        resident2.setName("李小花");
        resident2.setRole(User.Role.RESIDENT);
        resident2.setPhone("13800138002");
        resident2.setAddress("5号楼1单元302");
        userRepository.save(resident2);

        User resident3 = new User();
        resident3.setUsername("resident3");
        resident3.setName("王大力");
        resident3.setRole(User.Role.RESIDENT);
        resident3.setPhone("13800138003");
        resident3.setAddress("2号楼3单元601");
        userRepository.save(resident3);

        User property = new User();
        property.setUsername("property");
        property.setName("物业管理员");
        property.setRole(User.Role.PROPERTY);
        property.setPhone("13900139001");
        userRepository.save(property);

        User gardener = new User();
        gardener.setUsername("gardener");
        gardener.setName("园艺师老陈");
        gardener.setRole(User.Role.GARDENER);
        gardener.setPhone("13700137001");
        userRepository.save(gardener);

        User collector = new User();
        collector.setUsername("collector");
        collector.setName("回收员老赵");
        collector.setRole(User.Role.COLLECTOR);
        collector.setPhone("13600136001");
        userRepository.save(collector);
    }

    private void initPlots() {
        if (plotRepository.count() > 0) return;

        String[] plotNames = {"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"};
        String[] sunlightPeriods = {
            "上午6:00-10:00", "上午7:00-11:00", "全天日照",
            "上午8:00-12:00", "全天日照", "下午13:00-17:00",
            "上午6:00-9:00", "上午9:00-12:00", "下午14:00-18:00"
        };

        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Plot plot = new Plot();
                plot.setPlotCode(plotNames[index]);
                plot.setName(plotNames[index] + "号菜畦");
                plot.setArea(new BigDecimal("6.5"));
                plot.setPositionX(col * 120 + 20);
                plot.setPositionY(row * 120 + 20);
                plot.setWidth(100);
                plot.setHeight(100);
                plot.setStatus(index < 5 ? Plot.PlotStatus.AVAILABLE : Plot.PlotStatus.AVAILABLE);
                plot.setSunlightPeriod(sunlightPeriods[index]);
                plot.setSoilType("腐殖土");
                plot.setSoilPH("6.8");
                plot.setIrrigationType("滴灌");
                plotRepository.save(plot);
                index++;
            }
        }
    }

    private void initCompostBins() {
        if (binRepository.count() > 0) return;

        CompostBin bin1 = new CompostBin();
        bin1.setBinCode("BIN-001");
        bin1.setLocation("东侧堆肥区1号");
        bin1.setCapacity(new BigDecimal("200"));
        bin1.setCurrentWeight(new BigDecimal("45.5"));
        bin1.setStatus(CompostBin.BinStatus.FILLING);
        bin1.setBinType("好氧堆肥桶");
        binRepository.save(bin1);

        CompostBin bin2 = new CompostBin();
        bin2.setBinCode("BIN-002");
        bin2.setLocation("东侧堆肥区2号");
        bin2.setCapacity(new BigDecimal("200"));
        bin2.setCurrentWeight(new BigDecimal("180.0"));
        bin2.setStatus(CompostBin.BinStatus.FULL);
        bin2.setBinType("好氧堆肥桶");
        binRepository.save(bin2);

        CompostBin bin3 = new CompostBin();
        bin3.setBinCode("BIN-003");
        bin3.setLocation("西侧堆肥区1号");
        bin3.setCapacity(new BigDecimal("300"));
        bin3.setCurrentWeight(new BigDecimal("0"));
        bin3.setStatus(CompostBin.BinStatus.ACTIVE);
        bin3.setBinType("大型堆肥箱");
        binRepository.save(bin3);
    }

    private void initCompostBatches() {
        if (batchRepository.count() > 0) return;

        CompostBatch batch1 = new CompostBatch();
        batch1.setBatchCode("BATCH-2024-001");
        batch1.setSourceBinId(1L);
        batch1.setInitialWeight(new BigDecimal("150"));
        batch1.setMaterials("蔬菜叶、果皮、咖啡渣");
        batch1.setStartDate(LocalDate.now().minusDays(30));
        batch1.setExpectedMaturityDate(LocalDate.now().plusDays(30));
        batch1.setStatus(CompostBatch.BatchStatus.FERMENTING);
        batch1.setTurnCount(5);
        batch1.setLastTurnDate(LocalDate.now().minusDays(2));
        batch1.setMoistureLevel("适中");
        batchRepository.save(batch1);

        CompostBatch batch2 = new CompostBatch();
        batch2.setBatchCode("BATCH-2024-002");
        batch2.setSourceBinId(2L);
        batch2.setInitialWeight(new BigDecimal("180"));
        batch2.setFinalWeight(new BigDecimal("90"));
        batch2.setMaterials("厨余垃圾、落叶、秸秆");
        batch2.setStartDate(LocalDate.now().minusDays(75));
        batch2.setMaturityDate(LocalDate.now().minusDays(5));
        batch2.setStatus(CompostBatch.BatchStatus.MATURE);
        batch2.setTurnCount(12);
        batch2.setQualityGrade("A");
        batchRepository.save(batch2);

        CompostBatch batch3 = new CompostBatch();
        batch3.setBatchCode("BATCH-2024-003");
        batch3.setInitialWeight(new BigDecimal("50"));
        batch3.setStartDate(LocalDate.now().minusDays(5));
        batch3.setStatus(CompostBatch.BatchStatus.COLLECTING);
        batchRepository.save(batch3);
    }

    private void initToolKeys() {
        if (toolKeyRepository.count() > 0) return;

        ToolKey key1 = new ToolKey();
        key1.setKeyCode("TOOL-A01");
        key1.setCabinetNumber("A柜");
        key1.setDescription("工具柜A - 锄头、耙子等");
        key1.setStatus(ToolKey.KeyStatus.AVAILABLE);
        toolKeyRepository.save(key1);

        ToolKey key2 = new ToolKey();
        key2.setKeyCode("TOOL-A02");
        key2.setCabinetNumber("A柜");
        key2.setDescription("工具柜A - 浇水壶、喷雾器等");
        key2.setStatus(ToolKey.KeyStatus.BORROWED);
        key2.setCurrentBorrowerId(1L);
        toolKeyRepository.save(key2);

        ToolKey key3 = new ToolKey();
        key3.setKeyCode("TOOL-B01");
        key3.setCabinetNumber("B柜");
        key3.setDescription("工具柜B - 园艺剪、铲子等");
        key3.setStatus(ToolKey.KeyStatus.AVAILABLE);
        toolKeyRepository.save(key3);
    }

    private void initSettings() {
        if (settingRepository.count() > 0) return;

        RooftopSetting openTime = new RooftopSetting();
        openTime.setSettingKey("rooftop.open.time");
        openTime.setSettingValue("06:00-20:00");
        openTime.setDescription("屋顶菜园开放时间");
        settingRepository.save(openTime);

        RooftopSetting maxClaim = new RooftopSetting();
        maxClaim.setSettingKey("max.claim.per.family");
        maxClaim.setSettingValue("2");
        maxClaim.setDescription("每户最多认领菜畦数量");
        settingRepository.save(maxClaim);

        RooftopSetting waterFee = new RooftopSetting();
        waterFee.setSettingKey("water.fee.per.sqm");
        waterFee.setSettingValue("5.0");
        waterFee.setDescription("每平米每月水费（元）");
        settingRepository.save(waterFee);
    }

    private void initSampleClaimsAndPlantings() {
        if (claimRepository.count() > 0) return;

        PlotClaim claim1 = new PlotClaim();
        claim1.setPlotId(1L);
        claim1.setUserId(1L);
        claim1.setStatus(PlotClaim.ClaimStatus.ACTIVE);
        claim1.setClaimDate(LocalDate.now().minusDays(30));
        claim1.setStartDate(LocalDate.now().minusDays(25));
        claim1.setCropType("番茄");
        claim1.setWateringSchedule("每日早晚各一次");
        claim1.setWillingToDonateCompost(true);
        claim1.setHarvestPreference("部分捐赠给社区食堂");
        claimRepository.save(claim1);

        Plot plot1 = plotRepository.findById(1L).orElseThrow();
        plot1.setStatus(Plot.PlotStatus.PLANTING);
        plot1.setCurrentClaimId(claim1.getId());
        plot1.setCurrentCrop("番茄");
        plotRepository.save(plot1);

        PlantingRecord planting1 = new PlantingRecord();
        planting1.setPlotId(1L);
        planting1.setClaimId(claim1.getId());
        planting1.setUserId(1L);
        planting1.setCropName("番茄");
        planting1.setCropVariety("千禧小番茄");
        planting1.setSeedSource("社区园艺站");
        planting1.setSowingDate(LocalDate.now().minusDays(20));
        planting1.setTransplantDate(LocalDate.now().minusDays(10));
        planting1.setExpectedHarvestDate(LocalDate.now().plusDays(30));
        planting1.setPlantCount(12);
        planting1.setStatus(PlantingRecord.PlantingStatus.GROWING);
        plantingRepository.save(planting1);

        PlotClaim claim2 = new PlotClaim();
        claim2.setPlotId(2L);
        claim2.setUserId(2L);
        claim2.setStatus(PlotClaim.ClaimStatus.ACTIVE);
        claim2.setClaimDate(LocalDate.now().minusDays(20));
        claim2.setStartDate(LocalDate.now().minusDays(15));
        claim2.setCropType("生菜");
        claim2.setWateringSchedule("每日一次");
        claim2.setWillingToDonateCompost(true);
        claim2.setHarvestPreference("全部自用");
        claimRepository.save(claim2);

        Plot plot2 = plotRepository.findById(2L).orElseThrow();
        plot2.setStatus(Plot.PlotStatus.PLANTING);
        plot2.setCurrentClaimId(claim2.getId());
        plot2.setCurrentCrop("生菜");
        plotRepository.save(plot2);

        PlantingRecord planting2 = new PlantingRecord();
        planting2.setPlotId(2L);
        planting2.setClaimId(claim2.getId());
        planting2.setUserId(2L);
        planting2.setCropName("生菜");
        planting2.setCropVariety("奶油生菜");
        planting2.setSowingDate(LocalDate.now().minusDays(12));
        planting2.setExpectedHarvestDate(LocalDate.now().plusDays(18));
        planting2.setPlantCount(30);
        planting2.setStatus(PlantingRecord.PlantingStatus.GROWING);
        plantingRepository.save(planting2);

        HarvestRecord harvest1 = new HarvestRecord();
        harvest1.setPlotId(3L);
        harvest1.setPlantingId(0L);
        harvest1.setUserId(3L);
        harvest1.setCropName("小白菜");
        harvest1.setWeight(new BigDecimal("3.5"));
        harvest1.setUnit("公斤");
        harvest1.setQuality("优");
        harvest1.setDistributionType(HarvestRecord.DistributionType.SELF_USE);
        harvest1.setHarvestTime(java.time.LocalDateTime.now().minusDays(5));
        harvestRepository.save(harvest1);
    }
}
