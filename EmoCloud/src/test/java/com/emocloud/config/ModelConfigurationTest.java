package com.emocloud.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ModelConfiguration 单元测试
 */
@SpringBootTest
@TestPropertySource(properties = {
    "ai.model.base-url=https://api.test.com/v1",
    "ai.model.api-key=test-key-123",
    "ai.model.model=test-model",
    "ai.model.timeout=5000",
    "ai.model.max-retries=2",
    "ai.model.temperature=0.8",
    "ai.model.max-tokens=500"
})
class ModelConfigurationTest {

    @Autowired
    private ModelConfiguration modelConfiguration;

    @Test
    void testConfigurationLoading() {
        // 验证配置正确加载
        assertEquals("https://api.test.com/v1", modelConfiguration.getBaseUrl());
        assertEquals("test-key-123", modelConfiguration.getApiKey());
        assertEquals("test-model", modelConfiguration.getModel());
        assertEquals(5000, modelConfiguration.getTimeout());
        assertEquals(2, modelConfiguration.getMaxRetries());
        assertEquals(0.8, modelConfiguration.getTemperature());
        assertEquals(500, modelConfiguration.getMaxTokens());
    }

    @Test
    void testConfigurationValidation() {
        // 验证配置验证逻辑
        assertTrue(modelConfiguration.isValid());
    }

    @Test
    void testInvalidConfiguration() {
        // 测试无效配置
        ModelConfiguration invalidConfig = new ModelConfiguration();
        assertFalse(invalidConfig.isValid());
        
        // 测试部分无效配置
        invalidConfig.setBaseUrl("https://api.test.com");
        invalidConfig.setApiKey("test-key");
        // model 未设置，应该无效
        assertFalse(invalidConfig.isValid());
        
        // 设置所有必要参数
        invalidConfig.setModel("test-model");
        assertTrue(invalidConfig.isValid());
    }

    @Test
    void testParameterValidation() {
        ModelConfiguration config = new ModelConfiguration();
        config.setBaseUrl("https://api.test.com/v1");
        config.setApiKey("test-key");
        config.setModel("test-model");
        
        // 测试超时时间验证
        config.setTimeout(0);
        assertFalse(config.isValid());
        
        config.setTimeout(1000);
        assertTrue(config.isValid());
        
        // 测试温度参数验证
        config.setTemperature(-0.1);
        assertFalse(config.isValid());
        
        config.setTemperature(2.1);
        assertFalse(config.isValid());
        
        config.setTemperature(1.0);
        assertTrue(config.isValid());
        
        // 测试最大token验证
        config.setMaxTokens(0);
        assertFalse(config.isValid());
        
        config.setMaxTokens(1000);
        assertTrue(config.isValid());
    }
}