package com.emocloud.controller;

import com.emocloud.dto.EmotionCompanionDto.SceneModeDTO;
import com.emocloud.service.companion.SceneModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 场景模式控制器，处理与场景模式相关的 HTTP 请求
 */
@RestController
@RequestMapping("/scene-mode")
public class SceneModeController {

    @Autowired
    private SceneModeService sceneModeService;

    /**
     * 根据 ID 获取场景模式
     * @param sceneModeId 场景模式 ID
     * @return 场景模式信息响应
     */
    @GetMapping("/get-by-id/{sceneModeId}")
    public ResponseEntity<?> getSceneModeById(@PathVariable Long sceneModeId) {
        Optional<SceneModeDTO> sceneModeDTO = sceneModeService.getSceneModeById(sceneModeId);
        if (sceneModeDTO.isPresent()) {
            return new ResponseEntity<>(sceneModeDTO.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Scene mode not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 保存或更新场景模式
     * @param sceneModeDTO 场景模式信息 DTO
     * @return 保存或更新后的场景模式信息响应
     */
    @PostMapping("/save")
    public ResponseEntity<SceneModeDTO> saveSceneMode(@RequestBody SceneModeDTO sceneModeDTO) {
        SceneModeDTO savedSceneModeDTO = sceneModeService.saveSceneMode(sceneModeDTO);
        return new ResponseEntity<>(savedSceneModeDTO, HttpStatus.CREATED);
    }

    /**
     * 根据 ID 删除场景模式
     * @param sceneModeId 场景模式 ID
     * @return 删除结果响应
     */
    @DeleteMapping("/delete/{sceneModeId}")
    public ResponseEntity<String> deleteSceneMode(@PathVariable Long sceneModeId) {
        sceneModeService.deleteSceneMode(sceneModeId);
        return new ResponseEntity<>("Scene mode deleted successfully", HttpStatus.OK);
    }
}