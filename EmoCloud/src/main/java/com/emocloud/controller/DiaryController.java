package com.emocloud.controller;

import com.emocloud.model.emolog.Diary;
import com.emocloud.service.emolog.DiaryService;
import com.emocloud.dto.EmotionLog.DiaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * 情绪日记本控制器，处理与情绪日记本相关的 HTTP 请求
 */
@RestController
@RequestMapping("/emotion-log/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    /**
     * 获取情绪日记本详情
     * @param id 情绪日记本 ID
     * @return 情绪日记本详情响应
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDiaryDetail(@PathVariable Long id) {
        Optional<Diary> diary = diaryService.getDiaryById(id);
        if (diary.isPresent()) {
            DiaryDTO dto = diaryService.convertToDTO(diary.get());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Diary not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 更新情绪日记本信息
     * @param id 情绪日记本 ID
     * @param diaryDTO 情绪日记本 DTO
     * @return 更新后的情绪日记本信息响应
     */
    @PutMapping("/detail/{id}")
    public ResponseEntity<?> updateDiary(@PathVariable Long id, @RequestBody DiaryDTO diaryDTO) {
        Optional<Diary> existingDiary = diaryService.getDiaryById(id);
        if (existingDiary.isPresent()) {
            diaryDTO.setId(id);
            Diary updatedDiary = diaryService.updateDiary(diaryService.convertToEntity(diaryDTO));
            DiaryDTO dto = diaryService.convertToDTO(updatedDiary);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Diary not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * 创建新的情绪日记
     * @param diaryDTO 情绪日记本 DTO
     * @return 新创建的情绪日记本响应
     */
    @PostMapping("/create")
    public ResponseEntity<?> createDiary(@RequestBody DiaryDTO diaryDTO) {
        System.out.println("### 接收到创建日记请求 ###");
        System.out.println("请求参数: " + diaryDTO);

        try {
            Diary newDiary = diaryService.createDiary(diaryDTO);
            DiaryDTO dto = diaryService.convertToDTO(newDiary);
            System.out.println("### 日记创建成功 ###");
            System.out.println("创建的日记: " + dto);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("### 日记创建失败 ###");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace(); // 打印完整堆栈信息
            return new ResponseEntity<>("服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * 根据用户 ID 获取该用户的所有情绪日记本，并按日期排序
     * @param userId 用户 ID
     * @return 情绪日记本列表响应
     */
    @GetMapping("/user/{userId}/sorted")
    public ResponseEntity<?> getDiariesByUserIdSorted(@PathVariable Long userId) {
        List<Diary> diaries = diaryService.getDiariesByUserIdSorted(userId);
        List<DiaryDTO> dtos = diaries.stream().map(diaryService::convertToDTO).toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * AI生成日志摘要
     * @param content 对话内容
     * @param emotionTag 情绪标签
     * @return 生成的摘要响应
     */
    @GetMapping("/ai-summary")
    public ResponseEntity<?> generateAISummary(
            @RequestParam String content,
            @RequestParam String emotionTag
    ) {
        String summary = diaryService.generateAISummary(content, emotionTag);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

    /**
     * AI推荐音乐
     * @param emotionTag 情绪标签
     * @return 推荐的音乐URL响应
     */
    @GetMapping("/recommend-music")
    public ResponseEntity<?> recommendMusic(
            @RequestParam String emotionTag
    ) {
        String musicUrl = diaryService.recommendMusicByEmotion(emotionTag);
        return new ResponseEntity<>(musicUrl, HttpStatus.OK);
    }

    /**
     * 模拟从相册筛选图片
     * @param emotionTag 情绪标签
     * @return 图片URL列表响应
     */
    @GetMapping("/filter-images")
    public ResponseEntity<?> filterImages(
            @RequestParam String emotionTag
    ) {
        List<String> imageUrls = diaryService.filterImagesFromAlbum(emotionTag);
        return new ResponseEntity<>(imageUrls, HttpStatus.OK);
    }
    /**
     * 编辑日记内容
     * @param id 日记ID
     * @param diaryDTO 包含新内容的日记DTO
     * @return 更新后的日记DTO响应
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editDiary(@PathVariable Long id, @RequestBody DiaryDTO diaryDTO) {
        DiaryDTO updatedDiaryDTO = diaryService.editDiary(id, diaryDTO);
        if (updatedDiaryDTO != null) {
            return new ResponseEntity<>(updatedDiaryDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Diary not found", HttpStatus.NOT_FOUND);
        }
    }
    /**
     * 删除日记
     * @param id 日记ID
     * @return 删除结果响应
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id) {
        boolean result = diaryService.deleteDiary(id);
        if (result) {
            return new ResponseEntity<>("Diary deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Diary not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 根据日期查找日记
     * @param date 日期
     * @return 匹配的日记DTO列表响应
     */
    @GetMapping("/find/date")
    public ResponseEntity<?> findDiariesByDate(@RequestParam String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date); // 要求前端传ISO格式
        List<DiaryDTO> diaries = diaryService.findDiariesByDate(dateTime);
        return new ResponseEntity<>(diaries, HttpStatus.OK);
    }

    /**
     * 根据关键词查找日记
     * @param keyword 关键词
     * @return 匹配的日记DTO列表响应
     */
    @GetMapping("/find/keyword")
    public ResponseEntity<?> findDiariesByKeyword(@RequestParam String keyword) {
        List<DiaryDTO> diaries = diaryService.findDiariesByKeyword(keyword);
        return new ResponseEntity<>(diaries, HttpStatus.OK);
    }


    /**
     * 查询用户当天的日记
     * @param userId 用户ID
     * @return 如果找到，返回日记DTO；否则返回 404 Not Found
     */
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<?> getTodaysDiaryByUserId(@PathVariable Long userId) {
        Optional<Diary> diaryOpt = diaryService.findTodaysDiary(userId);
        if (diaryOpt.isPresent()) {
            return new ResponseEntity<>(diaryService.convertToDTO(diaryOpt.get()), HttpStatus.OK);
        } else {
            // 返回一个清晰的错误体，方便前端判断
            return new ResponseEntity<>("No diary found for today", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 追加内容到日记
     * @param id 日记ID
     * @param body 包含要追加内容的JSON对象，key为appendContent
     * @return 更新后的日记DTO响应
     */
    @PutMapping("/append/{id}")
    public ResponseEntity<?> appendDiaryContent(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String appendContent = body.get("appendContent");
        DiaryDTO updatedDiaryDTO = diaryService.appendDiaryContent(id, appendContent);
        if (updatedDiaryDTO != null) {
            return new ResponseEntity<>(updatedDiaryDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Diary not found", HttpStatus.NOT_FOUND);
        }
    }

}