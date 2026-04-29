package com.mfinsight.service;

import com.mfinsight.dto.EducationalContentDTO;
import com.mfinsight.entity.EducationalContent;
import com.mfinsight.entity.User;
import com.mfinsight.repository.EducationalContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalContentService {

    @Autowired private EducationalContentRepository contentRepository;
    @Autowired private AuthService authService;

    public EducationalContent create(EducationalContentDTO.CreateRequest request) {
        User author = authService.getCurrentUser();

        EducationalContent content = EducationalContent.builder()
                .title(request.getTitle())
                .contentType(request.getContentType())
                .content(request.getContent())
                .tags(request.getTags())
                .targetAudience(request.getTargetAudience())
                .author(author)
                .published(request.isPublished())
                .build();

        return contentRepository.save(content);
    }

    public EducationalContent update(Long id, EducationalContentDTO.UpdateRequest request) {
        EducationalContent content = getById(id);

        if (request.getTitle() != null) content.setTitle(request.getTitle());
        if (request.getContentType() != null) content.setContentType(request.getContentType());
        if (request.getContent() != null) content.setContent(request.getContent());
        if (request.getTags() != null) content.setTags(request.getTags());
        if (request.getTargetAudience() != null) content.setTargetAudience(request.getTargetAudience());
        if (request.getPublished() != null) content.setPublished(request.getPublished());

        return contentRepository.save(content);
    }

    public EducationalContent getById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Educational content not found with id: " + id));
    }

    public List<EducationalContent> getAllPublished() {
        return contentRepository.findByPublished(true);
    }

    public List<EducationalContent> getAll() {
        return contentRepository.findAll();
    }

    public List<EducationalContent> getByAuthor() {
        User author = authService.getCurrentUser();
        return contentRepository.findByAuthorId(author.getId());
    }

    public List<EducationalContent> search(String keyword) {
        return contentRepository.searchPublished(keyword);
    }

    public List<EducationalContent> getByAudience(String audience) {
        return contentRepository.findByTargetAudience(audience);
    }

    public void delete(Long id) {
        contentRepository.deleteById(id);
    }

    public EducationalContentDTO.Response toResponse(EducationalContent ec) {
        EducationalContentDTO.Response res = new EducationalContentDTO.Response();
        res.setId(ec.getId());
        res.setTitle(ec.getTitle());
        res.setContentType(ec.getContentType());
        res.setContent(ec.getContent());
        res.setTags(ec.getTags());
        res.setTargetAudience(ec.getTargetAudience());
        if (ec.getAuthor() != null) {
            res.setAuthorId(ec.getAuthor().getId());
            res.setAuthorName(ec.getAuthor().getFullName());
        }
        res.setPublished(ec.isPublished());
        res.setCreatedAt(ec.getCreatedAt());
        res.setUpdatedAt(ec.getUpdatedAt());
        return res;
    }
}
