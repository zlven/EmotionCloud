package com.emocloud.service.companion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 大模型服务类，负责与大模型进行交互，提供对话、聊天内容总结和虚拟场景对话等功能。
 */
@Service
public class LargeModelService {

    @Autowired
    private VivoLargeModelService vivoLargeModelService;

    /**
     * 调用大模型进行对话。
     * @param message 用户输入的消息
     * @param emotionTag 情绪标签
     * @return 大模型的回复，包含推荐信息
     */
    public String callLargeModel(String message, String emotionTag) {
        return vivoLargeModelService.callVivoModel(message, emotionTag);
    }

    /**
     * 调用大模型对聊天历史进行总结。
     * @param chatHistory 聊天历史记录列表
     * @return 聊天内容的总结
     */
    public String summarizeChatByLargeModel(List<String> chatHistory) {
        // 构建总结提示
        StringBuilder prompt = new StringBuilder("请总结以下对话内容：\n");
        for (String message : chatHistory) {
            prompt.append(message).append("\n");
        }

        // 调用Vivo模型进行总结
        return vivoLargeModelService.callVivoModel(prompt.toString(), null);
    }

    /**
     * 调用大模型进行虚拟场景对话。
     * @param message 用户输入的消息
     * @param sceneType 场景类型
     * @return 虚拟场景大模型的回复
     */
    public String callLargeModelForVirtualScene(String message, String sceneType) {
        // 构建场景化提示
        String prompt = String.format("你是一个%s场景中的角色。用户说：%s", sceneType, message);
        return vivoLargeModelService.callVivoModel(prompt, null);
    }

    /**
     * 根据情绪标签生成场景背景信息
     * @param emotionTag 情绪标签
     * @return 场景背景信息
     */
    public String generateSceneBackground(String emotionTag) {
        // 构建场景生成提示
        String prompt = String.format("请为情绪为%s的用户生成一个场景背景描述，用于虚拟场景交互。", emotionTag);
        return vivoLargeModelService.callVivoModel(prompt, null);
    }

    /**
    *调用大模型处理历史消息
    *
    * */
    public String callLargeModelWithHistory(List<String> chatHistory, String emotionTag) {
        // 构建提示
        StringBuilder prompt = new StringBuilder();
        for (String message : chatHistory) {
            prompt.append(message).append("\n");
        }
        // 调试：打印最终发送给大模型的提示
        System.out.println("=== 发送给大模型的最终提示 (情绪标签: " + emotionTag + ") ===");
        System.out.println(prompt.toString());
        System.out.println("=== 提示结束 ===");
        return callLargeModel(prompt.toString(), emotionTag);
    }
}