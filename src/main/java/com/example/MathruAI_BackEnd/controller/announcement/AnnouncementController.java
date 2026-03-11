package com.example.MathruAI_BackEnd.controller.announcement;

import com.example.MathruAI_BackEnd.dto.announcementDto.AnnouncementDto;
import com.example.MathruAI_BackEnd.service.impl.announcement.AnnouncementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    private final AnnouncementServiceImpl announcementService;

    @Autowired
    public AnnouncementController(AnnouncementServiceImpl announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementDto>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementDto>> getAllActiveAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllActiveAnnouncements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDto> getAnnouncementById(@PathVariable int id) {
        return ResponseEntity.ok(announcementService.getAnnouncementById(id));
    }

    @PostMapping
    public ResponseEntity<AnnouncementDto> createAnnouncement(@RequestBody AnnouncementDto announcementDto) {
        return ResponseEntity.ok(announcementService.createAnnouncement(announcementDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementDto> updateAnnouncement(@PathVariable int id, @RequestBody AnnouncementDto announcementDto) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, announcementDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable int id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}