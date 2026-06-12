package com.rooftop.garden.controller;

import com.rooftop.garden.entity.InspectionRecord;
import com.rooftop.garden.entity.ToolKey;
import com.rooftop.garden.entity.VisitorRecord;
import com.rooftop.garden.entity.RooftopSetting;
import com.rooftop.garden.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/inspections")
    public List<InspectionRecord> getAllInspections() {
        return propertyService.getAllInspections();
    }

    @GetMapping("/inspections/{id}")
    public ResponseEntity<InspectionRecord> getInspectionById(@PathVariable Long id) {
        return propertyService.getInspectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/inspections/inspector/{inspectorId}")
    public List<InspectionRecord> getInspectionsByInspectorId(@PathVariable Long inspectorId) {
        return propertyService.getInspectionsByInspectorId(inspectorId);
    }

    @GetMapping("/inspections/unhandled")
    public List<InspectionRecord> getUnhandledInspections() {
        return propertyService.getUnhandledInspections();
    }

    @PostMapping("/inspections")
    public InspectionRecord createInspection(@RequestBody InspectionRecord inspection) {
        return propertyService.createInspection(inspection);
    }

    @PostMapping("/inspections/{id}/handle")
    public ResponseEntity<InspectionRecord> handleInspection(@PathVariable Long id,
                                                              @RequestParam String handledBy,
                                                              @RequestParam(required = false) String remark) {
        try {
            return ResponseEntity.ok(propertyService.handleInspection(id, handledBy, remark));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/tool-keys")
    public List<ToolKey> getAllToolKeys() {
        return propertyService.getAllToolKeys();
    }

    @GetMapping("/tool-keys/{id}")
    public ResponseEntity<ToolKey> getToolKeyById(@PathVariable Long id) {
        return propertyService.getToolKeyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tool-keys/available")
    public List<ToolKey> getAvailableToolKeys() {
        return propertyService.getAvailableToolKeys();
    }

    @PostMapping("/tool-keys")
    public ToolKey createToolKey(@RequestBody ToolKey toolKey) {
        return propertyService.createToolKey(toolKey);
    }

    @PostMapping("/tool-keys/{id}/borrow")
    public ResponseEntity<ToolKey> borrowKey(@PathVariable Long id,
                                              @RequestParam Long borrowerId,
                                              @RequestParam(required = false, defaultValue = "24") Integer expectedHours) {
        try {
            return ResponseEntity.ok(propertyService.borrowKey(id, borrowerId, expectedHours));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/tool-keys/{id}/return")
    public ResponseEntity<ToolKey> returnKey(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(propertyService.returnKey(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/visitors")
    public List<VisitorRecord> getAllVisitors() {
        return propertyService.getAllVisitors();
    }

    @GetMapping("/visitors/{id}")
    public ResponseEntity<VisitorRecord> getVisitorById(@PathVariable Long id) {
        return propertyService.getVisitorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/visitors/visiting")
    public List<VisitorRecord> getVisitingVisitors() {
        return propertyService.getVisitingVisitors();
    }

    @PostMapping("/visitors")
    public VisitorRecord createVisitor(@RequestBody VisitorRecord visitor) {
        return propertyService.createVisitor(visitor);
    }

    @PostMapping("/visitors/{id}/checkin")
    public ResponseEntity<VisitorRecord> checkInVisitor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(propertyService.checkInVisitor(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/visitors/{id}/checkout")
    public ResponseEntity<VisitorRecord> checkOutVisitor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(propertyService.checkOutVisitor(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/settings")
    public List<RooftopSetting> getAllSettings() {
        return propertyService.getAllSettings();
    }

    @GetMapping("/settings/{key}")
    public ResponseEntity<RooftopSetting> getSettingByKey(@PathVariable String key) {
        return propertyService.getSettingByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/settings")
    public RooftopSetting saveSetting(@RequestBody RooftopSetting setting) {
        return propertyService.saveSetting(setting);
    }
}
