package com.mfinsight.repository;

import com.mfinsight.entity.EducationalContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationalContentRepository extends JpaRepository<EducationalContent, Long> {

    List<EducationalContent> findByPublished(boolean published);

    List<EducationalContent> findByAuthorId(Long authorId);

    List<EducationalContent> findByTargetAudience(String targetAudience);

    List<EducationalContent> findByContentType(String contentType);

    @Query("SELECT e FROM EducationalContent e WHERE e.published = true AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<EducationalContent> searchPublished(@Param("keyword") String keyword);
}
