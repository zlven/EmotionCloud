package com.emocloud.repository.emolog;

import com.emocloud.model.emolog.EmotionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 情绪日志仓库接口：定义数据库查询方法（JPA自动实现）
 */
public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {

    /**
     * 根据用户ID查询日志（不分页）
     */
    List<EmotionLog> findByUserId(Long userId);

    /**
     * 根据用户ID和时间范围查询日志（分页）
     */
    @Query("SELECT e FROM EmotionLog e WHERE e.user.id = :userId " +
            "AND (:start IS NULL OR e.cratetime >= :start) " +
            "AND e.cratetime <= :end")
    Page<EmotionLog> findByUserIdAndCratetimeBetween(
            @Param("userId") Long userId,
            @Param("start") String start,
            @Param("end") String end,
            Pageable pageable
    );

    /**
     * 根据用户ID、时间范围和关键词查询日志（分页）
     * 修复：使用 MEMBER OF 查询 tags 列表字段
     */
    @Query("SELECT e FROM EmotionLog e WHERE e.user.id = :userId " +
            "AND (:start IS NULL OR e.cratetime >= :start) " +
            "AND e.cratetime <= :end " +
            "AND (LOWER(e.conclusion) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR :keyword MEMBER OF e.tags)")
    Page<EmotionLog> findByUserIdAndCratetimeBetweenAndConclusionContainingOrTagsContaining(
            @Param("userId") Long userId,
            @Param("start") String start,
            @Param("end") String end,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    /**
     * 根据用户ID和时间范围查询日志（不分页）
     */
    List<EmotionLog> findByUserIdAndCratetimeBetween(
            @Param("userId") Long userId,
            @Param("start") String start,
            @Param("end") String end
    );
}