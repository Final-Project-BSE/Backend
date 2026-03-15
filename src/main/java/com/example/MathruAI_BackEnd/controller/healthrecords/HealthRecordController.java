package com.example.MathruAI_BackEnd.controller.healthrecords;

import com.example.MathruAI_BackEnd.dto.healthrecords.HealthCategoryResponseDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.HealthRecordRequestDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.HealthRecordResponseDto;
import com.example.MathruAI_BackEnd.service.interservice.healthrecords.HealthRecordServiceInter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.MathruAI_BackEnd.service.impl.healthrecords.FileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
@Tag(name = "Health Records", description = "Management of health records and categories")
public class HealthRecordController {

    private final HealthRecordServiceInter healthRecordService;
    private final FileUploadService fileUploadService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Fetch all health categories with record counts for the logged-in user")
    public ResponseEntity<List<HealthCategoryResponseDto>> getCategories() {
        return ResponseEntity.ok(healthRecordService.getAllCategoriesWithCounts(getCurrentUserEmail()));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get records by category", description = "Fetch all health records for a specific category")
    public ResponseEntity<List<HealthRecordResponseDto>> getRecordsByCategory(
            @PathVariable("categoryId") UUID categoryId) {
        return ResponseEntity.ok(healthRecordService.getRecordsByCategory(getCurrentUserEmail(), categoryId));
    }

    @PostMapping(value = "/category/{categoryId}", consumes = "multipart/form-data")
    @Operation(summary = "Create record", description = "Create a new health record with optional file attachments")
    public ResponseEntity<HealthRecordResponseDto> createRecord(
            @PathVariable("categoryId") UUID categoryId,
            @ModelAttribute HealthRecordRequestDto request) throws IOException {
        return ResponseEntity.ok(healthRecordService.createRecord(getCurrentUserEmail(), categoryId, request));
    }

    @GetMapping("/record/{recordId}")
    @Operation(summary = "Get record details", description = "Fetch single record details including file links")
    public ResponseEntity<HealthRecordResponseDto> getRecordDetails(@PathVariable("recordId") UUID recordId) {
        return ResponseEntity.ok(healthRecordService.getRecordDetails(recordId));
    }

    @PutMapping(value = "/record/{recordId}", consumes = "multipart/form-data")
    @Operation(summary = "Update record", description = "Update record details and/or add new files")
    public ResponseEntity<HealthRecordResponseDto> updateRecord(
            @PathVariable("recordId") UUID recordId,
            @ModelAttribute HealthRecordRequestDto request) throws IOException {
        return ResponseEntity.ok(healthRecordService.updateRecord(recordId, request));
    }

    @DeleteMapping("/record/{recordId}")
    @Operation(summary = "Delete record", description = "Delete a record and its associated files")
    public ResponseEntity<Void> deleteRecord(@PathVariable("recordId") UUID recordId) throws IOException {
        healthRecordService.deleteRecord(recordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/files/{fileName:.+}")
    @Operation(summary = "Get file", description = "Serve a health record file securely")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName, HttpServletRequest request) {
        try {
            Resource resource = fileUploadService.loadFileAsResource(fileName);
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                // Ignore
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
