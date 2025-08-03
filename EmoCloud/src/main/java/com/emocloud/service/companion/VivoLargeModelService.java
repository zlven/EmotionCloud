package com.emocloud.service.companion;

import com.emocloud.config.VivoAuthConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Vivo大模型服务类，负责与Vivo大模型API进行交互
 */
@Service
public class VivoLargeModelService {

    private final String appId = "2025794232";
    private final String appKey = "tXRvrqyAYuhYfmla";
    private final String DOMAIN = "api-ai.vivo.com.cn";
    private final String URI = "/vivogpt/completions";
    private final String METHOD = "POST";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public VivoLargeModelService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 调用Vivo大模型进行对话
     * @param prompt 用户输入的提示
     * @param emotionTag 情绪标签
     * @return 大模型的回复
     */
    public String callVivoModel(String prompt, String emotionTag) {
        try {
            // 添加情绪标签作为上下文
            String enhancedPrompt = buildEnhancedPrompt(prompt, emotionTag);

            // 构建请求参数
            Map<String, Object> queryParams = new HashMap<>();
            UUID requestId = UUID.randomUUID();
            queryParams.put("requestId", requestId.toString());

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("prompt", enhancedPrompt);
            requestBody.put("model", "vivo-BlueLM-TB-Pro");
            UUID sessionId = UUID.randomUUID();
            requestBody.put("sessionId", sessionId.toString());

            // 生成认证头
            String queryStr = mapToQueryString(queryParams);
            HttpHeaders headers = VivoAuthConfig.generateAuthHeaders(appId, appKey, METHOD, URI, queryStr);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求实体
            String requestBodyString = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, headers);

            // 发送请求
            String url = String.format("http://%s%s?%s", DOMAIN, URI, queryStr);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseVivoResponse(response.getBody());
            } else {
                throw new RuntimeException("Vivo API调用失败: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("调用Vivo大模型时出错", e);
        }
    }

    /**
     * 构建增强提示，包含情绪信息
     * @param prompt 用户原始提示
     * @param emotionTag 情绪标签
     * @return 增强后的提示
     */
    private String buildEnhancedPrompt(String prompt, String emotionTag) {
        if (emotionTag != null && !emotionTag.isEmpty()) {
            return String.format("用户当前情绪为%s，请以适当的语气回复：%s", emotionTag, prompt);
        }
        return prompt;
    }

    /**
     * 解析Vivo模型的响应
     * @param responseBody 响应体
     * @return 提取的回复文本
     */
    private String parseVivoResponse(String responseBody) {
        System.out.println("解析Vivo响应: " + responseBody);
        try {
            // 将响应解析为Map
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            // 获取data字段
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
            if (data != null) {
                // 从data中获取content字段（之前错误地使用了"text"）
                return (String) data.getOrDefault("content", "");
            }
            return "";
        } catch (Exception e) {
            System.err.println("解析Vivo响应失败: " + e.getMessage());
            return responseBody;
        }
    }



    /**
     * 将Map转换为查询字符串
     * @param map 参数Map
     * @return 查询字符串
     */
    private String mapToQueryString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder queryStringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (queryStringBuilder.length() > 0) {
                queryStringBuilder.append("&");
            }
            queryStringBuilder.append(entry.getKey());
            queryStringBuilder.append("=");
            queryStringBuilder.append(entry.getValue());
        }
        return queryStringBuilder.toString();
    }
}