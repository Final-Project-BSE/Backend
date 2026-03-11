package com.example.MathruAI_BackEnd.repository.announcement;

import com.example.MathruAI_BackEnd.entity.announcement.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByActiveTrue();
}