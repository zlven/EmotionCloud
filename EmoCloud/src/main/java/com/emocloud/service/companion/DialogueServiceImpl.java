package com.emocloud.service.companion;

import com.emocloud.dto.EmotionCompanionDto.DialogueDTO;
import com.emocloud.dto.EmotionCompanionDto.MessageDTO;
import com.emocloud.dto.EmotionCompanionDto.SceneModeDTO;
import com.emocloud.model.EmotionTag;
import com.emocloud.model.User;
import com.emocloud.model.companion.Dialogue;
import com.emocloud.model.companion.Message;
import com.emocloud.model.companion.SceneMode;
import com.emocloud.repository.companion.DialogueRepository;
import com.emocloud.repository.companion.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 对话服务实现类，处理用户与AI的对话会话业务逻辑
 */
@Service
@Transactional
public class DialogueServiceImpl implements DialogueService {

    @Autowired
    private DialogueRepository dialogueRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private LargeModelService largeModelService;

    // === Dialogue 实现 ===
    /**
     * 创建新对话
     * @param dialogueDTO 对话数据传输对象
     * @return 创建后的对话DTO
     */
    @Override
    public DialogueDTO createDialogue(DialogueDTO dialogueDTO) {
        Dialogue dialogue = convertToEntity(dialogueDTO);
        Dialogue savedDialogue = dialogueRepository.save(dialogue);

        // 确保返回的 DTO 包含新创建的对话ID
        DialogueDTO result = convertToDTO(savedDialogue);
        System.out.println("创建对话成功，ID: " + result.getId());
        return result;
    }

    /**
     * 根据ID获取对话
     * @param dialogueId 对话ID
     * @return 对话DTO的Optional对象
     */
    @Override
    public Optional<DialogueDTO> getDialogueById(Long dialogueId) {
        return dialogueRepository.findById(dialogueId)
                .map(this::convertToDTO);
    }

    /**
     * 更新对话信息
     * @param dialogueId 对话ID
     * @param dialogueDTO 包含更新信息的对话DTO
     * @return 更新后的对话DTO
     */
    @Override
    public DialogueDTO updateDialogue(Long dialogueId, DialogueDTO dialogueDTO) {
        return dialogueRepository.findById(dialogueId)
                .map(existingDialogue -> {
                    // 更新对话信息
                    existingDialogue.setMode(dialogueDTO.getMode());
                    existingDialogue.setSummary(dialogueDTO.getSummary());
                    existingDialogue.setActive(dialogueDTO.getActive());
                    // 保存更新
                    Dialogue updatedDialogue = dialogueRepository.save(existingDialogue);
                    return convertToDTO(updatedDialogue);
                })
                .orElseThrow(() -> new IllegalArgumentException("对话不存在: " + dialogueId));
    }

    /**
     * 关闭对话（设置为非活跃状态）
     * @param dialogueId 对话ID
     */
    @Override
    public void closeDialogue(Long dialogueId) {
        dialogueRepository.findById(dialogueId)
                .ifPresent(dialogue -> {
                    dialogue.setActive(false);
                    dialogueRepository.save(dialogue);
                });
    }

    /**
     * 获取用户的对话列表（分页）
     * @param userId 用户ID
     * @param pageable 分页信息
     * @return 对话DTO的分页对象
     */
    @Override
    public Page<DialogueDTO> getDialoguesByUser(Long userId, Pageable pageable) {
        Page<Dialogue> dialogues = dialogueRepository.findByUserId(userId, pageable);
        List<DialogueDTO> dialogueDTOs = dialogues.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dialogueDTOs, pageable, dialogues.getTotalElements());
    }

    // === Message 实现 ===
    @Override
    public MessageDTO sendMessageAndGetResponse(MessageDTO messageDTO) {
        // 1. 保存用户消息
        Message userMessage = convertToEntity(messageDTO);
        Message savedUserMessage = messageRepository.save(userMessage);

        // 2. 获取对话信息以获取情绪标签
        Optional<Dialogue> dialogueOptional = dialogueRepository.findById(messageDTO.getDialogueId());
        String emotionTag = "neutral"; // 默认情绪
        if (dialogueOptional.isPresent()) {
            EmotionTag emotionTagEntity = dialogueOptional.get().getEmotionTag();
            if (emotionTagEntity != null) {
                emotionTag = emotionTagEntity.getName();
            }
        }

        // 3. 获取历史消息作为上下文
        Page<Message> historyMessages = messageRepository.findByDialogueId(messageDTO.getDialogueId(), Pageable.unpaged());
        List<String> chatHistory = historyMessages.getContent().stream()
                .map(Message::getContent)
                .collect(Collectors.toList());
        chatHistory.add(savedUserMessage.getContent());


        // 调试：打印完整的提示词
        System.out.println("=== 传递给大模型的完整提示词 ===");
        for (String message : chatHistory) {
            System.out.println(message);
        }
        System.out.println("=== 提示词结束 ===");

        // 4. 调用大模型获取回复
        String aiResponse = largeModelService.callLargeModelWithHistory(chatHistory, emotionTag);

        // 5. 保存AI回复
        Message aiMessage = new Message();
        aiMessage.setDialogue(savedUserMessage.getDialogue());
        aiMessage.setSenderType("AI");
        aiMessage.setContent(aiResponse);
        aiMessage.setContentType("TEXT");
        aiMessage.setEmotionScore(0.0);
        aiMessage.setFunctionCall("");
        Message savedAiMessage = messageRepository.save(aiMessage);

        // 6. 返回AI回复的DTO
        return convertToDTO(savedAiMessage);
    }
    /**
     * 发送消息
     * @param messageDTO 消息数据传输对象
     * @return 保存后的消息DTO
     */
    @Override
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        Message message = convertToEntity(messageDTO);
        Message savedMessage = messageRepository.save(message);

        // 获取对话信息
        Optional<Dialogue> dialogueOptional = dialogueRepository.findById(message.getDialogue().getId());
        if (dialogueOptional.isPresent()) {
            Dialogue dialogue = dialogueOptional.get();
            String emotionTag = dialogue.getEmotionTag() != null ? dialogue.getEmotionTag().getName() : null;

            // 调用大模型获取回复
            String response = largeModelService.callLargeModel(message.getContent(), emotionTag);

            // 创建回复消息
            Message responseMessage = new Message();
            responseMessage.setDialogue(dialogue);
            responseMessage.setSenderType("AI");
            responseMessage.setContent(response);
            responseMessage.setContentType("TEXT");
            responseMessage.setEmotionScore(0.0);
            responseMessage.setFunctionCall("");
            Message savedResponseMessage = messageRepository.save(responseMessage);

            // 记录用户习惯（简单示例，可根据实际情况扩展）
            recordUserHabits(message.getContent(), dialogue);

            return convertToDTO(savedResponseMessage);
        }

        return convertToDTO(savedMessage);
    }

    /**
     * 记录用户习惯
     * @param messageContent 用户输入的消息内容
     * @param dialogue 对话信息
     */
    private void recordUserHabits(String messageContent, Dialogue dialogue) {
        // 简单示例：如果消息中包含运动或游玩相关关键词，则记录到数据库
        if (messageContent.contains("运动") || messageContent.contains("游玩")) {
            // 这里可以实现具体的数据库记录逻辑
            System.out.println("记录用户习惯：" + messageContent + " 到对话 " + dialogue.getId());
        }
    }

    /**
     * 根据ID获取消息
     * @param messageId 消息ID
     * @return 消息DTO的Optional对象
     */
    @Override
    public Optional<MessageDTO> getMessageById(Long messageId) {
        return messageRepository.findById(messageId)
                .map(this::convertToDTO);
    }

    /**
     * 获取对话中的消息列表（分页）
     * @param dialogueId 对话ID
     * @param pageable 分页信息
     * @return 消息DTO的分页对象
     */
    @Override
    public Page<MessageDTO> getMessagesByDialogue(Long dialogueId, Pageable pageable) {
        Page<Message> messages = messageRepository.findByDialogueId(dialogueId, pageable);
        List<MessageDTO> messageDTOs = messages.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(messageDTOs, pageable, messages.getTotalElements());
    }

    // === SceneMode 实现 ===
    /**
     * 更新对话的场景模式
     * @param dialogueId 对话ID
     * @param sceneModeDTO 场景模式数据传输对象
     * @return 更新后的对话DTO
     */
    @Override
    public DialogueDTO updateSceneMode(Long dialogueId, SceneModeDTO sceneModeDTO) {
        return dialogueRepository.findById(dialogueId)
                .map(dialogue -> {
                    // 更新场景模式
                    SceneMode sceneMode = dialogue.getSceneMode();
                    if (sceneMode == null) {
                        sceneMode = new SceneMode();
                        sceneMode.setDialogue(dialogue);
                    }
                    sceneMode.setSceneName(sceneModeDTO.getSceneName());
                    sceneMode.setBackgroundImage(sceneModeDTO.getBackgroundImage());
                    sceneMode.setBackgroundMusic(sceneModeDTO.getBackgroundMusic());
                    sceneMode.setNpcRole(sceneModeDTO.getNpcRole());

                    // 保存更新
                    dialogue.setSceneMode(sceneMode);
                    Dialogue updatedDialogue = dialogueRepository.save(dialogue);
                    return convertToDTO(updatedDialogue);
                })
                .orElseThrow(() -> new IllegalArgumentException("对话不存在: " + dialogueId));
    }

    /**
     * 将Dialogue实体转换为DTO
     * @param dialogue 对话实体
     * @return 对话DTO
     */
    @Override
    public DialogueDTO convertToDTO(Dialogue dialogue) {
        DialogueDTO dto = new DialogueDTO();
        dto.setId(dialogue.getId());
        dto.setUserId(dialogue.getUser().getId());
        dto.setEmotionTagId(dialogue.getEmotionTag() != null ? dialogue.getEmotionTag().getId() : null);
        dto.setMode(dialogue.getMode());
        dto.setSummary(dialogue.getSummary());
        dto.setMessageIds(dialogue.getMessages().stream().map(Message::getId).collect(Collectors.toList()));
        dto.setSceneModeId(dialogue.getSceneMode() != null ? dialogue.getSceneMode().getId() : null);
        dto.setActive(dialogue.getActive());
        dto.setCreateTime(dialogue.getCreateTime());
        dto.setUpdateTime(dialogue.getUpdateTime());
        return dto;
    }

    /**
     * 将DialogueDTO转换为实体
     * @param dialogueDTO 对话DTO
     * @return 对话实体
     */
    @Override
    public Dialogue convertToEntity(DialogueDTO dialogueDTO) {
        Dialogue dialogue = new Dialogue();
        dialogue.setId(dialogueDTO.getId());
        // 这里需要根据实际情况设置 User 和 EmotionTag 对象
        dialogue.setMode(dialogueDTO.getMode());
        dialogue.setSummary(dialogueDTO.getSummary());
        dialogue.setActive(dialogueDTO.getActive());
        dialogue.setCreateTime(dialogueDTO.getCreateTime());
        dialogue.setUpdateTime(dialogueDTO.getUpdateTime());

        if (dialogueDTO.getUserId() != null) {
            User user = new User();
            user.setId(dialogueDTO.getUserId());
            dialogue.setUser(user);
        }

        return dialogue;
    }

    /**
     * 将Message实体转换为DTO
     * @param message 消息实体
     * @return 消息DTO
     */
    @Override
    public MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setDialogueId(message.getDialogue().getId());
        dto.setSenderType(message.getSenderType());
        dto.setContent(message.getContent());
        dto.setContentType(message.getContentType());
        dto.setEmotionScore(message.getEmotionScore());
        dto.setFunctionCall(message.getFunctionCall());
        dto.setCreateTime(message.getCreateTime());
        return dto;
    }

    /**
     * 将MessageDTO转换为实体
     * @param messageDTO 消息DTO
     * @return 消息实体
     */
    @Override
    public Message convertToEntity(MessageDTO messageDTO) {
        Message message = new Message();
        message.setId(messageDTO.getId());
        message.setSenderType(messageDTO.getSenderType());
        message.setContent(messageDTO.getContent());
        message.setContentType(messageDTO.getContentType());
        message.setEmotionScore(messageDTO.getEmotionScore());
        message.setFunctionCall(messageDTO.getFunctionCall());
        message.setCreateTime(messageDTO.getCreateTime());

        // 关键：设置 Dialogue 关联
        if (messageDTO.getDialogueId() != null) {
            Dialogue dialogue = new Dialogue();
            dialogue.setId(messageDTO.getDialogueId());
            message.setDialogue(dialogue);
        } else {
            throw new IllegalArgumentException("对话ID不能为空");
        }

        return message;
    }
    /**
     * 总结对话内容
     * @param dialogueId 对话ID
     * @return 对话总结
     */
    public String summarizeDialogue(Long dialogueId) {
        Page<Message> messages = messageRepository.findByDialogueId(dialogueId, Pageable.unpaged());
        List<String> chatHistory = messages.getContent().stream()
                .map(Message::getContent)
                .collect(Collectors.toList());
        String summary = largeModelService.summarizeChatByLargeModel(chatHistory);

        // 新增：保存summary到数据库
        Optional<Dialogue> dialogueOpt = dialogueRepository.findById(dialogueId);
        if (dialogueOpt.isPresent()) {
            Dialogue dialogue = dialogueOpt.get();
            dialogue.setSummary(summary);
            dialogueRepository.save(dialogue);
        }

        return summary;
    }
}