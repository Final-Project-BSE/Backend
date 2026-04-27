package com.example.MathruAI_BackEnd.service.impl.checklist;

import com.example.MathruAI_BackEnd.dto.checklist.UserChecklistDto;
import com.example.MathruAI_BackEnd.entity.checklist.Checklist;
import com.example.MathruAI_BackEnd.entity.checklist.UserChecklistStatus;
import com.example.MathruAI_BackEnd.repository.checklist.ChecklistRepository;
import com.example.MathruAI_BackEnd.repository.checklist.UserChecklistStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository checklistRepo;
    private final UserChecklistStatusRepository statusRepo;

    public List<Checklist> getMasterItemsForMidwife(Long midwifeId) {
        return checklistRepo.findByMidwifeId(midwifeId);
    }

    public Checklist create(Long midwifeId, Checklist item) {
        item.setId(null);
        item.setMidwifeId(midwifeId);
        return checklistRepo.save(item);
    }

    public Checklist update(Long midwifeId, Long id, Checklist updated) {
        Checklist item = checklistRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        if (!item.getMidwifeId().equals(midwifeId)) {
            throw new RuntimeException("You cannot update another midwife's checklist item");
        }

        item.setName(updated.getName());
        item.setQuantity(updated.getQuantity());
        item.setCategory(updated.getCategory());

        return checklistRepo.save(item);
    }

    @Transactional
    public void delete(Long midwifeId, Long id) {
        Checklist item = checklistRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        if (!item.getMidwifeId().equals(midwifeId)) {
            throw new RuntimeException("You cannot delete another midwife's checklist item");
        }

        statusRepo.deleteByChecklist(item);
        checklistRepo.delete(item);
    }

    public List<UserChecklistDto> getChecklistForUser(Long userId, Long midwifeId) {
        return checklistRepo.findByMidwifeId(midwifeId)
                .stream()
                .map(item -> {
                    boolean checked = statusRepo.findByUserIdAndChecklist(userId, item)
                            .map(UserChecklistStatus::isChecked)
                            .orElse(false);

                    return UserChecklistDto.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .quantity(item.getQuantity())
                            .category(item.getCategory())
                            .checked(checked)
                            .build();
                })
                .toList();
    }

    public UserChecklistDto toggleUserItem(Long userId, Long midwifeId, Long checklistId) {
        Checklist checklist = checklistRepo.findById(checklistId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        if (!checklist.getMidwifeId().equals(midwifeId)) {
            throw new RuntimeException("This checklist item does not belong to this user's midwife");
        }

        UserChecklistStatus status = statusRepo.findByUserIdAndChecklist(userId, checklist)
                .orElse(
                        UserChecklistStatus.builder()
                                .userId(userId)
                                .checklist(checklist)
                                .checked(false)
                                .build()
                );

        status.setChecked(!status.isChecked());
        statusRepo.save(status);

        return UserChecklistDto.builder()
                .id(checklist.getId())
                .name(checklist.getName())
                .quantity(checklist.getQuantity())
                .category(checklist.getCategory())
                .checked(status.isChecked())
                .build();
    }
}