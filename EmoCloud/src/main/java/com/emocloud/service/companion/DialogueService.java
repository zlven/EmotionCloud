package com.emocloud.service.companion;

import com.emocloud.dto.EmotionCompanionDto.DialogueDTO;
import com.emocloud.dto.EmotionCompanionDto.MessageDTO;
import com.emocloud.model.companion.Dialogue;
import com.emocloud.model.companion.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 对话服务接口，管理用户与AI的对话会话
 */
public interface DialogueService {
    // === Dialogue 相关方法 ===
    /**
     * 创建新对话
     * @param dialogueDTO 对话请求DTO
     * @return 创建后的对话DTO
     */
    DialogueDTO createDialogue(DialogueDTO dialogueDTO);

    /**
     * 根据ID获取对话
     * @param dialogueId 对话ID
     * @return 对话DTO（Optional可能为空）
     */
    Optional<DialogueDTO> getDialogueById(Long dialogueId);

    /**
     * 更新对话信息
     * @param dialogueId 对话ID
     * @param dialogueDTO 更新内容
     * @return 更新后的对话DTO
     */
    DialogueDTO updateDialogue(Long dialogueId, DialogueDTO dialogueDTO);

    /**
     * 关闭对话
     * @param dialogueId 对话ID
     */
    void closeDialogue(Long dialogueId);

    /**
     * 获取用户的所有对话
     * @param userId 用户ID
     * @param pageable 分页信息
     * @return 对话分页列表
     */
    Page<DialogueDTO> getDialoguesByUser(Long userId, Pageable pageable);

    // === Message 相关方法 ===
    /**
     * 发送消息并返回AI回复
     */
    MessageDTO sendMessageAndGetResponse(MessageDTO messageDTO);

    /**
     * 发送消息到对话
     * @param messageDTO 消息请求DTO
     * @return 发送后的消息DTO
     */
    MessageDTO sendMessage(MessageDTO messageDTO);

    /**
     * 根据ID获取消息
     * @param messageId 消息ID
     * @return 消息DTO（Optional可能为空）
     */
    Optional<MessageDTO> getMessageById(Long messageId);

    /**
     * 获取对话中的所有消息
     * @param dialogueId 对话ID
     * @param pageable 分页信息
     * @return 消息分页列表
     */
    Page<MessageDTO> getMessagesByDialogue(Long dialogueId, Pageable pageable);

    // === SceneMode 相关方法 ===
    /**
     * 更新对话的场景模式
     * @param dialogueId 对话ID
     * @param sceneModeDTO 场景模式DTO
     * @return 更新后的对话DTO
     */
    DialogueDTO updateSceneMode(Long dialogueId, com.emocloud.dto.EmotionCompanionDto.SceneModeDTO sceneModeDTO);

    // === 辅助方法（Model与DTO转换） ===
    DialogueDTO convertToDTO(Dialogue dialogue);
    Dialogue convertToEntity(DialogueDTO dialogueDTO);
    MessageDTO convertToDTO(Message message);
    Message convertToEntity(MessageDTO messageDTO);

    /**
     * 总结对话内容
     * @param dialogueId 对话ID
     * @return 对话总结
     */
    String summarizeDialogue(Long dialogueId);
}