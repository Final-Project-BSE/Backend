package com.example.MathruAI_BackEnd.service.interservice.announcement;

import com.example.MathruAI_BackEnd.dto.announcementDto.AnnouncementDto;

import java.util.List;

public interface AnnouncementServiceInter {
    List<AnnouncementDto> getAllAnnouncements();
    List<AnnouncementDto> getAllActiveAnnouncements();
    AnnouncementDto getAnnouncementById(int id);
    AnnouncementDto createAnnouncement(AnnouncementDto announcementDto);
    AnnouncementDto updateAnnouncement(int id, AnnouncementDto announcementDto);
    void deleteAnnouncement(int id);
}