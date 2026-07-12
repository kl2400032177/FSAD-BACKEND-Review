package com.mfinsight.controller;

import com.mfinsight.dto.ApiResponse;
import com.mfinsight.dto.EducationalContentDTO;
import com.mfinsight.entity.EducationalContent;
import com.mfinsight.service.EducationalContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/education")
@CrossOrigin(origins = "*")
public class EducationalContentController {

    @Autowired
    private EducationalContentService contentService;

    // ─── PUBLIC ENDPOINTS ─────────────────────────────────────

    /**
     * GET /api/education/public
     * Fetch all published educational content (no login required).
     */
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<EducationalContentDTO.Response>>> getAllPublished() {
        List<EducationalContentDTO.Response> list = contentService.getAllPublished()
                .stream().map(contentService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Published content fetched"));
    }

    /**
     * GET /api/education/public/{id}
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<EducationalContentDTO.Response>> getPublicById(@PathVariable Long id) {
        EducationalContent content = contentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(contentService.toResponse(content), "Content fetched"));
    }

    /**
     * GET /api/education/public/search?keyword=sip
     */
    @GetMapping("/public/search")
    public ResponseEntity<ApiResponse<List<EducationalContentDTO.Response>>> searchPublic(
            @RequestParam String keyword) {
        List<EducationalContentDTO.Response> list = contentService.search(keyword)
                .stream().map(contentService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Search results"));
    }

    /**
     * GET /api/education/public/audience?level=BEGINNER
     */
    @GetMapping("/public/audience")
    public ResponseEntity<ApiResponse<List<EducationalContentDTO.Response>>> getByAudience(
            @RequestParam String level) {
        List<EducationalContentDTO.Response> list = contentService.getByAudience(level)
                .stream().map(contentService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Content for " + level));
    }

    // ─── ADVISOR ENDPOINTS ────────────────────────────────────

    /**
     * POST /api/education
     * Financial Advisor creates new educational content.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('FINANCIAL_ADVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<EducationalContentDTO.Response>> create(
            @Valid @RequestBody EducationalContentDTO.CreateRequest request) {
        EducationalContent content = contentService.create(request);
        return ResponseEntity.ok(ApiResponse.success(contentService.toResponse(content), "Content created"));
    }

    /**
     * PUT /api/education/{id}
     * Update educational content.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FINANCIAL_ADVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<EducationalContentDTO.Response>> update(
            @PathVariable Long id,
            @RequestBody EducationalContentDTO.UpdateRequest request) {
        EducationalContent content = contentService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(contentService.toResponse(content), "Content updated"));
    }

    /**
     * GET /api/education/my
     * Advisor views their own created content.
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('FINANCIAL_ADVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<EducationalContentDTO.Response>>> getMyContent() {
        List<EducationalContentDTO.Response> list = contentService.getByAuthor()
                .stream().map(contentService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Your content"));
    }

    /**
     * PATCH /api/education/{id}/publish
     * Publish or unpublish content.
     */
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('FINANCIAL_ADVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<EducationalContentDTO.Response>> togglePublish(
            @PathVariable Long id,
            @RequestParam boolean publish) {
        EducationalContentDTO.UpdateRequest req = new EducationalContentDTO.UpdateRequest();
        req.setPublished(publish);
        EducationalContent content = contentService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success(contentService.toResponse(content),
                publish ? "Content published" : "Content unpublished"));
    }

    /**
     * DELETE /api/education/{id}
     * Admin or Advisor deletes content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FINANCIAL_ADVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        contentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Content deleted", "Deleted successfully"));
    }

    // ─── ADMIN ────────────────────────────────────────────────

    /**
     * GET /api/education/all
     * Admin views all content including unpublished.
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EducationalContentDTO.Response>>> getAll() {
        List<EducationalContentDTO.Response> list = contentService.getAll()
                .stream().map(contentService::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "All content"));
    }
}
