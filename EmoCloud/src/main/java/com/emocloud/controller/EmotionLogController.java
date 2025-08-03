package com.emocloud.controller;

import com.emocloud.dto.EmotionLog.EmotionLogDTO;
import com.emocloud.model.emolog.EmotionLog;
import com.emocloud.service.emolog.EmotionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 情绪日志控制器：处理前端HTTP请求
 */
@RestController
@RequestMapping("/emotion-log")
public class EmotionLogController {

    @Autowired
    private EmotionLogService emotionLogService;

    /**
     * 获取单个日志详情
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getEmotionLogDetail(@PathVariable Long id) {
        Optional<EmotionLog> emotionLog = emotionLogService.getEmotionLogById(id);
        if (emotionLog.isPresent()) {
            return new ResponseEntity<>(emotionLogService.convertToDTO(emotionLog.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("日志不存在", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 更新日志
     */
    @PutMapping("/detail/{id}")
    public ResponseEntity<?> updateEmotionLog(
            @PathVariable Long id,
            @RequestBody EmotionLogDTO emotionLogDTO) {
        Optional<EmotionLog> existingLog = emotionLogService.getEmotionLogById(id);
        if (existingLog.isPresent()) {
            EmotionLog log = emotionLogService.convertToEntity(emotionLogDTO);
            log.setId(id); // 确保ID一致（避免创建新日志）
            EmotionLog updated = emotionLogService.updateEmotionLog(log);
            return new ResponseEntity<>(emotionLogService.convertToDTO(updated), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("日志不存在", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 获取所有日志（分页）
     */
    @GetMapping("/list")
    public ResponseEntity<Page<EmotionLogDTO>> getAllEmotionLogs(Pageable pageable) {
        Page<EmotionLog> logs = emotionLogService.getAllEmotionLogs(pageable);
        Page<EmotionLogDTO> dtos = logs.map(emotionLogService::convertToDTO);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * 核心接口：根据用户ID查询日志（支持分页、时间筛选、关键词搜索）
     */
    @GetMapping("/user/{userId}/list")
    public ResponseEntity<Page<EmotionLogDTO>> getEmotionLogsByUserId(
            @PathVariable Long userId,
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "all") String filterType, // 默认为全部
            @RequestParam(required = false) String keyword) { // 可选关键词

        Page<EmotionLog> logs = emotionLogService.getEmotionLogsByUserId(
                userId, pageable, filterType, keyword
        );
        Page<EmotionLogDTO> dtos = logs.map(emotionLogService::convertToDTO);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /**
     * 创建新日志
     */
    @PostMapping("/create")
    public ResponseEntity<EmotionLogDTO> createEmotionLog(@RequestBody EmotionLogDTO dto) {
        EmotionLog log = emotionLogService.convertToEntity(dto);
        EmotionLog created = emotionLogService.createEmotionLog(log);
        return new ResponseEntity<>(emotionLogService.convertToDTO(created), HttpStatus.CREATED);
    }

    /**
     * 删除日志
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmotionLog(@PathVariable Long id) {
        emotionLogService.deleteEmotionLog(id);
        return new ResponseEntity<>("日志删除成功", HttpStatus.OK);
    }

    /**
     * 根据日期范围查询用户日志（不分页）
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<EmotionLogDTO>> getByDateRange(
            @PathVariable Long userId,
            @RequestParam String startDate, // 格式：yyyy-MM-dd
            @RequestParam String endDate) { // 格式：yyyy-MM-dd

        List<EmotionLog> logs = emotionLogService.getEmotionLogsByDateRange(
                userId,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );
        List<EmotionLogDTO> dtos = logs.stream()
                .map(emotionLogService::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}