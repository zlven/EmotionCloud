# EmoCloud 情感云平台

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

EmoCloud是一个基于Spring Boot开发的情感管理和社交平台，旨在帮助用户记录、分析和管理情感状态，同时提供AI情感陪伴和社交互动功能。

## ✨ 主要功能

### 🎭 情感记录与分析
- **情感日志**：记录日常情感状态和心情变化
- **智能日记**：AI辅助生成和分析情感日记
- **情感标签**：多维度情感分类和标记系统
- **数据可视化**：情感趋势分析和统计报告

### 🤖 AI情感陪伴
- **智能对话**：基于大语言模型的情感陪伴聊天
- **场景模式**：多种情感支持场景（倾听、鼓励、建议等）
- **个性化回复**：根据用户情感状态提供定制化回应
- **情感分析**：实时分析用户情感状态并给出建议

### 🐾 虚拟宠物系统
- **情感宠物**：根据用户情感状态变化的虚拟宠物
- **互动养成**：通过情感记录和积极行为养成宠物
- **状态同步**：宠物状态反映用户的情感健康程度

### 🌐 社交互动
- **情感分享**：发布情感动态和心情状态
- **社区支持**：用户间的情感支持和互助
- **评论互动**：针对情感分享的温暖评论和鼓励
- **图片分享**：支持图片上传和分享

### 🎵 多媒体支持
- **音乐推荐**：根据情感状态推荐合适的音乐
- **视频内容**：情感调节和心理健康相关视频

## 🏗️ 技术架构

### 后端技术栈
- **框架**：Spring Boot 3.4.4
- **安全**：Spring Security
- **数据访问**：Spring Data JPA
- **数据库**：MySQL 8.0
- **验证**：Jakarta Validation
- **对象映射**：ModelMapper
- **AI集成**：Vivo大模型API

### 项目结构
```
src/main/java/com/emocloud/
├── config/          # 配置类
├── controller/      # REST API控制器
├── dto/            # 数据传输对象
├── exception/      # 自定义异常
├── model/          # 实体模型
├── repository/     # 数据访问层
└── service/        # 业务逻辑层
```

## 🚀 快速开始

### 环境要求
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Git

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/[your-username]/EmoCloud.git
cd EmoCloud
```

2. **数据库配置**
```sql
-- 创建数据库
CREATE DATABASE emocloud CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **配置文件**
编辑 `src/main/resources/application.properties`：
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/emocloud?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password

# AI模型配置（可选）
ai.model.base-url=your_ai_api_url
ai.model.api-key=your_api_key
```

4. **构建和运行**
```bash
# 使用Maven构建
./mvnw clean install

# 运行应用
./mvnw spring-boot:run
```

5. **访问应用**
打开浏览器访问：`http://localhost:8080`

## 📚 API文档

### 用户管理
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/users/profile` - 获取用户信息

### 情感记录
- `POST /api/emotion-logs` - 创建情感记录
- `GET /api/emotion-logs` - 获取情感记录列表
- `GET /api/diaries` - 获取日记列表
- `POST /api/diaries` - 创建日记

### AI陪伴
- `POST /api/companions/dialogue` - 创建对话
- `POST /api/companions/message` - 发送消息
- `GET /api/scene-modes` - 获取场景模式

### 社交功能
- `POST /api/social/posts` - 发布动态
- `GET /api/social/posts` - 获取动态列表
- `POST /api/social/comments` - 发表评论

### 宠物系统
- `GET /api/pets` - 获取宠物状态
- `POST /api/pets/interact` - 与宠物互动

## 🛠️ 开发指南

### 代码规范
- 遵循Java编码规范
- 使用驼峰命名法
- 添加适当的注释和文档
- 编写单元测试

### 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 分支管理
- `main` - 主分支，稳定版本
- `develop` - 开发分支
- `feature/*` - 功能分支
- `hotfix/*` - 热修复分支

## 🤝 贡献指南

我们欢迎所有形式的贡献！

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 贡献类型
- 🐛 Bug修复
- ✨ 新功能开发
- 📝 文档改进
- 🎨 UI/UX优化
- ⚡ 性能优化
- 🔒 安全增强

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 强大的Java框架
- [MySQL](https://www.mysql.com/) - 可靠的数据库系统
- [Vivo AI](https://ai.vivo.com.cn/) - AI大模型支持
- 所有贡献者和支持者

## 📞 联系我们

- 项目主页：[GitHub Repository](https://github.com/[your-username]/EmoCloud)
- 问题反馈：[Issues](https://github.com/[your-username]/EmoCloud/issues)
- 邮箱：[your-email]

## 🗺️ 路线图

### v1.0.0 (当前版本)
- ✅ 基础情感记录功能
- ✅ AI情感陪伴
- ✅ 社交分享功能
- ✅ 虚拟宠物系统

### v1.1.0 (计划中)
- 🔄 情感数据可视化
- 🔄 移动端适配
- 🔄 多语言支持
- 🔄 情感分析报告

### v2.0.0 (未来版本)
- 📋 群组功能
- 📋 专业心理咨询师接入
- 📋 情感健康评估
- 📋 个性化推荐算法优化

---

⭐ 如果这个项目对你有帮助，请给我们一个星标！

💝 让我们一起构建一个更温暖的情感支持社区！