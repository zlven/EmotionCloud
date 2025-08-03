package com.emocloud.service.companion;

import com.emocloud.dto.EmotionCompanionDto.SceneModeDTO;
import com.emocloud.model.companion.SceneMode;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 场景模式服务接口，管理对话的场景设置
 */
@Service
public interface SceneModeService {
    /**
     * 根据ID获取场景模式
     * @param sceneModeId 场景模式ID
     * @return 场景模式DTO（Optional可能为空）
     */
    Optional<SceneModeDTO> getSceneModeById(Long sceneModeId);

    /**
     * 创建或更新场景模式
     * @param sceneModeDTO 场景模式DTO
     * @return 保存后的场景模式DTO
     */
    SceneModeDTO saveSceneMode(SceneModeDTO sceneModeDTO);

    /**
     * 删除场景模式
     * @param sceneModeId 场景模式ID
     */
    void deleteSceneMode(Long sceneModeId);

    // === 辅助方法（Model与DTO转换） ===
    SceneModeDTO convertToDTO(SceneMode sceneMode);
    SceneMode convertToEntity(SceneModeDTO sceneModeDTO);
}