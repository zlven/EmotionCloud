package com.emocloud.service.emolog;

import com.emocloud.model.emolog.Diary;
import com.emocloud.dto.EmotionLog.DiaryDTO;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

/**
 * 情绪日记服务接口，定义了情绪日记相关的业务操作
 */
public interface DiaryService {
    /**
     * 根据情绪日记本 ID 获取情绪日记本信息
     * @param id 情绪日记本 ID
     * @return 可选的情绪日记本信息，可能为空
     */
    Optional<Diary> getDiaryById(Long id);

    /**
     * 更新情绪日记本信息
     * @param diary 情绪日记本信息
     * @return 更新后的情绪日记本信息
     */
    Diary updateDiary(Diary diary);

    /**
     * 将 Diary 实体转换为 DiaryDTO
     * @param diary 情绪日记实体
     * @return 情绪日记 DTO
     */
    DiaryDTO convertToDTO(Diary diary);

    /**
     * 将 DiaryDTO 转换为 Diary 实体
     * @param diaryDTO 情绪日记 DTO
     * @return 情绪日记实体
     */
    Diary convertToEntity(DiaryDTO diaryDTO);

    /**
     * 创建新的情绪日记
     * @param diaryDTO 情绪日记 DTO
     * @return 新创建的情绪日记实体
     */
    Diary createDiary(DiaryDTO diaryDTO);

    /**
     * 根据用户 ID 获取所有情绪日记
     * @param userId 用户 ID
     * @return 情绪日记列表
     */
    List<Diary> getDiariesByUserId(Long userId);

    /**
     * AI生成日志摘要
     * @param content 对话内容
     * @param emotionTag 情绪标签
     * @return 生成的摘要
     */
    String generateAISummary(String content, String emotionTag);

    /**
     * AI推荐音乐
     * @param emotionTag 情绪标签
     * @return 推荐的音乐URL
     */
    String recommendMusicByEmotion(String emotionTag);

    /**
     * 从相册筛选图片（模拟，实际需对接相册服务）
     * @param emotionTag 情绪标签
     * @return 图片URL列表
     */
    List<String> filterImagesFromAlbum(String emotionTag);
    /**
     * 根据用户ID获取该用户的所有日记，并按创建时间排序
     * @param userId 用户ID
     * @return 按创建时间排序的日记列表
     */
    List<Diary> getDiariesByUserIdSorted(Long userId);

    /**
     * 编辑日记内容
     * @param id 日记ID
     * @param diaryDTO 包含新内容的日记DTO
     * @return 更新后的日记DTO
     */
    DiaryDTO editDiary(Long id, DiaryDTO diaryDTO);
    /**
     * 删除日记
     * @param id 日记ID
     * @return 删除结果
     */
    boolean deleteDiary(Long id);
    /**
     * 根据日期查找日记
     * @param date 日期
     * @return 匹配的日记DTO列表
     */
    List<DiaryDTO> findDiariesByDate(LocalDateTime date);

    /**
     * 根据关键词查找日记
     * @param keyword 关键词
     * @return 匹配的日记DTO列表
     */
    List<DiaryDTO> findDiariesByKeyword(String keyword);


    /**
     * 查询用户当天的日记
     * @param userId 用户ID
     * @return 如果找到，返回日记DTO；否则返回 404 Not Found
     */
    Optional<Diary> findTodaysDiary(Long userId);

    /**
     * 追加内容到日记
     * @param id 日记ID
     * @param appendContent 要追加的内容
     * @return 更新后的日记DTO
     */
    DiaryDTO appendDiaryContent(Long id, String appendContent);
}