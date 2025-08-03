package com.emocloud.service.emolog;

import com.emocloud.dto.EmotionLog.EmotionLogDTO;
import com.emocloud.model.emolog.EmotionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 情绪日志服务接口：定义核心业务逻辑
 */
public interface EmotionLogService {

    /**
     * 根据ID查询日志
     * @param id 日志ID
     * @return 包含日志的Optional对象
     */
    Optional<EmotionLog> getEmotionLogById(Long id);

    /**
     * 更新日志
     * @param emotionLog 待更新的日志实体
     * @return 更新后的日志实体
     */
    EmotionLog updateEmotionLog(EmotionLog emotionLog);

    /**
     * 创建新日志（自动补全时间）
     * @param emotionLog 待创建的日志实体
     * @return 新创建的日志实体
     */
    EmotionLog createEmotionLog(EmotionLog emotionLog);

    /**
     * 根据ID删除日志
     * @param id 日志ID
     */
    void deleteEmotionLog(Long id);

    /**
     * 查询所有日志（不分页）
     * @return 日志列表
     */
    List<EmotionLog> getAllEmotionLogs();

    /**
     * 查询所有日志（分页）
     * @param pageable 分页参数（页码、每页条数、排序）
     * @return 分页后的日志列表
     */
    Page<EmotionLog> getAllEmotionLogs(Pageable pageable);

    /**
     * 根据用户ID查询日志（不分页）
     * @param userId 用户ID
     * @return 该用户的日志列表
     */
    List<EmotionLog> getEmotionLogsByUserId(Long userId);

    /**
     * 根据用户ID查询日志（分页+筛选）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @param filterType 时间筛选类型（all/week/month）
     * @param keyword 搜索关键词（匹配结论或标签）
     * @return 分页后的日志列表
     */
    Page<EmotionLog> getEmotionLogsByUserId(Long userId, Pageable pageable, String filterType, String keyword);

    /**
     * 根据日期范围查询用户日志
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 符合条件的日志列表
     */
    List<EmotionLog> getEmotionLogsByDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 实体类转DTO（隐藏敏感字段，如User对象）
     * @param emotionLog 日志实体
     * @return 日志DTO
     */
    EmotionLogDTO convertToDTO(EmotionLog emotionLog);

    /**
     * DTO转实体类（用于接收前端提交的数据）
     * @param emotionLogDTO 日志DTO
     * @return 日志实体
     */
    EmotionLog convertToEntity(EmotionLogDTO emotionLogDTO);
}