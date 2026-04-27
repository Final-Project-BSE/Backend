package com.example.MathruAI_BackEnd.controller.checklist;

import com.example.MathruAI_BackEnd.dto.checklist.UserChecklistDto;
import com.example.MathruAI_BackEnd.entity.checklist.Checklist;
import com.example.MathruAI_BackEnd.service.impl.checklist.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklist")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService service;

    // MIDWIFE SIDE

    @GetMapping("/master/midwife/{midwifeId}")
    public List<Checklist> getMasterChecklist(@PathVariable Long midwifeId) {
        return service.getMasterItemsForMidwife(midwifeId);
    }

    @PostMapping("/master/midwife/{midwifeId}")
    public Checklist create(
            @PathVariable Long midwifeId,
            @RequestBody Checklist item
    ) {
        return service.create(midwifeId, item);
    }

    @PutMapping("/master/midwife/{midwifeId}/{id}")
    public Checklist update(
            @PathVariable Long midwifeId,
            @PathVariable Long id,
            @RequestBody Checklist item
    ) {
        return service.update(midwifeId, id, item);
    }

    @DeleteMapping("/master/midwife/{midwifeId}/{id}")
    public void delete(
            @PathVariable Long midwifeId,
            @PathVariable Long id
    ) {
        service.delete(midwifeId, id);
    }

    // USER SIDE

    @GetMapping("/user/{userId}/midwife/{midwifeId}")
    public List<UserChecklistDto> getUserChecklist(
            @PathVariable Long userId,
            @PathVariable Long midwifeId
    ) {
        return service.getChecklistForUser(userId, midwifeId);
    }

    @PatchMapping("/user/{userId}/midwife/{midwifeId}/toggle/{checklistId}")
    public UserChecklistDto toggleUserChecklistItem(
            @PathVariable Long userId,
            @PathVariable Long midwifeId,
            @PathVariable Long checklistId
    ) {
        return service.toggleUserItem(userId, midwifeId, checklistId);
    }
}