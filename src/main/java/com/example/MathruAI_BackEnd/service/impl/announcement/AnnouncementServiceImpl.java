package com.example.MathruAI_BackEnd.service.impl.announcement;

import com.example.MathruAI_BackEnd.dto.announcementDto.AnnouncementDto;
import com.example.MathruAI_BackEnd.entity.announcement.Announcement;
import com.example.MathruAI_BackEnd.repository.announcement.AnnouncementRepository;
import com.example.MathruAI_BackEnd.service.interservice.announcement.AnnouncementServiceInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementServiceInter {

    private final AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    private AnnouncementDto toDto(Announcement announcement) {
        AnnouncementDto dto = new AnnouncementDto();
        dto.setAnnouncementId(announcement.getAnnouncementId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setCategory(announcement.getCategory());
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setActive(announcement.isActive());
        return dto;
    }

    @Override
    public List<AnnouncementDto> getAllAnnouncements() {
        List<Announcement> announcements = announcementRepository.findAll();
        List<AnnouncementDto> announcementDtos = new ArrayList<>();
        for (Announcement announcement : announcements) {
            announcementDtos.add(toDto(announcement));
        }
        return announcementDtos;
    }

    @Override
    public List<AnnouncementDto> getAllActiveAnnouncements() {
        List<Announcement> announcements = announcementRepository.findByActiveTrue();
        List<AnnouncementDto> announcementDtos = new ArrayList<>();
        for (Announcement announcement : announcements) {
            announcementDtos.add(toDto(announcement));
        }
        return announcementDtos;
    }

    @Override
    public AnnouncementDto getAnnouncementById(int id) {
        Announcement announcement = announcementRepository.findById(id).get();
        return toDto(announcement);
    }

    @Override
    public AnnouncementDto createAnnouncement(AnnouncementDto announcementDto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(announcementDto.getTitle());
        announcement.setContent(announcementDto.getContent());
        announcement.setCategory(announcementDto.getCategory());
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setActive(announcementDto.isActive());

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        return toDto(savedAnnouncement);
    }

    @Override
    public AnnouncementDto updateAnnouncement(int id, AnnouncementDto announcementDto) {
        Announcement announcement = announcementRepository.findById(id).get();

        announcement.setTitle(announcementDto.getTitle());
        announcement.setContent(announcementDto.getContent());
        announcement.setCategory(announcementDto.getCategory());
        announcement.setActive(announcementDto.isActive());

        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        return toDto(updatedAnnouncement);
    }

    @Override
    public void deleteAnnouncement(int id) {
        announcementRepository.deleteById(id);
    }
}