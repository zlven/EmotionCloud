package com.emocloud.service.companion;

import com.emocloud.dto.EmotionCompanionDto.SceneModeDTO;
import com.emocloud.model.companion.SceneMode;
import com.emocloud.repository.companion.SceneModeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 场景模式服务实现类，管理对话的场景设置业务逻辑
 * 提供场景模式的增删改查功能，以及DTO与实体之间的转换
 */
@Service
@Transactional
public class SceneModeServiceImpl implements SceneModeService {

    @Autowired
    private SceneModeRepository sceneModeRepository;

    /**
     * 根据ID获取场景模式
     * @param sceneModeId 场景模式ID
     * @return 包含场景模式DTO的Optional对象，如果不存在则返回空的Optional
     */
    @Override
    public Optional<SceneModeDTO> getSceneModeById(Long sceneModeId) {
        return sceneModeRepository.findById(sceneModeId)
                .map(this::convertToDTO);
    }

    /**
     * 保存或更新场景模式
     * 如果ID存在则更新现有记录，否则创建新记录
     * @param sceneModeDTO 场景模式DTO
     * @return 保存后的场景模式DTO
     */
    @Override
    public SceneModeDTO saveSceneMode(SceneModeDTO sceneModeDTO) {
        SceneMode sceneMode = convertToEntity(sceneModeDTO);
        SceneMode savedSceneMode = sceneModeRepository.save(sceneMode);
        return convertToDTO(savedSceneMode);
    }

    /**
     * 根据ID删除场景模式
     * @param sceneModeId 场景模式ID
     */
    @Override
    public void deleteSceneMode(Long sceneModeId) {
        sceneModeRepository.deleteById(sceneModeId);
    }

    /**
     * 将场景模式实体转换为DTO
     * @param sceneMode 场景模式实体
     * @return 场景模式DTO
     */
    @Override
    public SceneModeDTO convertToDTO(SceneMode sceneMode) {
        SceneModeDTO dto = new SceneModeDTO();
        dto.setId(sceneMode.getId());
        dto.setDialogueId(sceneMode.getDialogue().getId());
        dto.setSceneName(sceneMode.getSceneName());
        dto.setBackgroundImage(sceneMode.getBackgroundImage());
        dto.setBackgroundMusic(sceneMode.getBackgroundMusic());
        dto.setNpcRole(sceneMode.getNpcRole());
        return dto;
    }

    /**
     * 将场景模式DTO转换为实体
     * @param sceneModeDTO 场景模式DTO
     * @return 场景模式实体
     */
    @Override
    public SceneMode convertToEntity(SceneModeDTO sceneModeDTO) {
        SceneMode sceneMode = new SceneMode();
        sceneMode.setId(sceneModeDTO.getId());
        sceneMode.getDialogue().setId(sceneModeDTO.getDialogueId());
        sceneMode.setSceneName(sceneModeDTO.getSceneName());
        sceneMode.setBackgroundImage(sceneModeDTO.getBackgroundImage());
        sceneMode.setBackgroundMusic(sceneModeDTO.getBackgroundMusic());
        sceneMode.setNpcRole(sceneModeDTO.getNpcRole());
        return sceneMode;
    }
}