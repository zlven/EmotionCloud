# 实施计划

- [ ] 1. 创建配置管理组件




  - 创建ModelConfiguration配置类，支持从application.properties读取AI模型配置
  - 实现配置验证逻辑，确保必要参数完整性
  - 添加配置加载日志记录
  - _需求: 1.1, 1.3, 4.1_

- [-] 2. 创建OpenAI兼容的请求/响应模型


  - 创建OpenAIRequest类，包含messages、model、temperature等字段
  - 创建OpenAIResponse类，包含choices、message等字段
  - 创建Message内部类，支持role和content字段
  - 添加JSON序列化注解
  - _需求: 2.1, 2.2, 5.1_

- [ ] 3. 实现统一模型服务类
  - 创建UnifiedModelService类，替代现有的VivoLargeModelService依赖
  - 实现callModel方法，支持OpenAI格式的API调用
  - 实现HTTP请求发送和响应解析逻辑
  - 添加请求头设置（Authorization、Content-Type等）
  - _需求: 2.1, 2.2, 5.1_

- [ ] 4. 实现情绪标签和上下文处理
  - 在UnifiedModelService中实现buildEnhancedPrompt方法
  - 支持情绪标签融入到系统消息中
  - 实现消息历史格式化为OpenAI messages格式
  - 添加场景类型处理逻辑
  - _需求: 5.1, 5.2, 5.4_

- [ ] 5. 实现聊天总结功能
  - 在UnifiedModelService中实现summarizeChat方法
  - 将聊天历史转换为OpenAI消息格式
  - 构建总结提示词
  - 处理长文本截断逻辑
  - _需求: 5.2_

- [ ] 6. 实现虚拟场景对话功能
  - 在UnifiedModelService中实现callForVirtualScene方法
  - 构建场景化提示词模板
  - 支持不同场景类型的角色设定
  - 集成情绪标签到场景对话中
  - _需求: 5.3_

- [ ] 7. 实现场景背景生成功能
  - 在UnifiedModelService中实现generateSceneBackground方法
  - 基于情绪标签构建背景生成提示
  - 返回适合的场景描述内容
  - _需求: 5.4_

- [ ] 8. 实现错误处理和重试机制
  - 创建ModelServiceException和ModelConfigurationException异常类
  - 在UnifiedModelService中添加try-catch错误处理
  - 实现@Retryable注解的重试逻辑
  - 添加@Recover降级处理方法
  - 记录详细的错误日志
  - _需求: 6.2_

- [ ] 9. 更新LargeModelService以使用新的统一服务
  - 将LargeModelService中的VivoLargeModelService依赖替换为UnifiedModelService
  - 更新callLargeModel方法调用新的统一接口
  - 更新summarizeChatByLargeModel方法
  - 更新callLargeModelForVirtualScene方法
  - 更新generateSceneBackground方法
  - 更新callLargeModelWithHistory方法
  - _需求: 2.1, 2.2_

- [ ] 10. 添加配置到application.properties
  - 在application.properties中添加ai.model配置项
  - 设置DeepSeek API的默认配置示例
  - 添加配置注释说明不同模型的配置方法
  - _需求: 1.1, 4.1_

- [ ] 11. 创建常量类和工具方法
  - 创建ModelConstants类，定义角色常量和提示模板
  - 实现提示词构建的工具方法
  - 添加消息格式化工具方法
  - _需求: 5.1, 5.2, 5.3, 5.4_

- [ ] 12. 实现健康检查功能
  - 创建ModelHealthIndicator类实现HealthIndicator接口
  - 实现health方法，发送测试请求检查模型可用性
  - 返回详细的健康状态信息
  - _需求: 6.1_

- [ ] 13. 添加日志记录和监控
  - 在UnifiedModelService中添加结构化日志记录
  - 记录API调用的请求和响应信息
  - 添加性能监控日志（响应时间等）
  - 记录重试和错误信息
  - _需求: 6.1, 6.4_

- [ ] 14. 编写单元测试
  - 为ModelConfiguration创建单元测试
  - 为UnifiedModelService创建单元测试，使用Mock RestTemplate
  - 测试各种场景的API调用逻辑
  - 测试错误处理和重试机制
  - _需求: 2.1, 2.2_

- [ ] 15. 编写集成测试
  - 创建端到端测试，测试完整的对话流程
  - 测试聊天历史处理功能
  - 测试虚拟场景对话功能
  - 测试配置加载和验证
  - _需求: 2.1, 5.1, 5.2, 5.3_

- [ ] 16. 清理和重构遗留代码
  - 删除或重构VivoLargeModelService类（可选保留作为备用）
  - 删除VivoAuthConfig类（如果不再需要）
  - 清理不再使用的依赖和导入
  - 更新相关的注释和文档
  - _需求: 2.2_