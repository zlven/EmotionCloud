package com.emocloud.service.user;

import com.emocloud.dto.EmotionCompanionDto.DialogueDTO;
import com.emocloud.dto.UserDTO;
import com.emocloud.exception.UserAlreadyExistsException;
import com.emocloud.exception.UserNotFoundException;
import com.emocloud.model.User;
import com.emocloud.model.companion.Dialogue;
import com.emocloud.repository.UserRepository;
import com.emocloud.repository.companion.DialogueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务实现类，提供用户管理和认证核心功能
 * 1. 处理用户注册、登录流程
 * 2. 自动关联对话表创建逻辑
 * 3. 实体与DTO转换管理
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final DialogueRepository dialogueRepository;

    /**
     * 构造函数依赖注入
     * @param userRepository 用户数据访问层
     * @param dialogueRepository 对话数据访问层
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, DialogueRepository dialogueRepository) {
        this.userRepository = userRepository;
        this.dialogueRepository = dialogueRepository;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("查询用户信息，ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(User user) {
        logger.info("更新用户信息，ID: {}", user.getId());
        return userRepository.save(user);
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        // 1. 校验用户名唯一性
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("注册失败，用户名已存在: " + userDTO.getUsername());
        }

        // 2. DTO转实体
        User newUser = convertToEntity(userDTO);

        // 3. 设置注册时间
        String registerTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        newUser.setRegistertime(registerTime);

        // 4. 持久化用户
        User savedUser = userRepository.save(newUser);
        logger.info("用户注册成功，ID: {}", savedUser.getId());

        // 5. 自动创建对话表（核心逻辑）
        try {
            createDialogueForUser(savedUser.getId());
            logger.info("为用户创建对话表成功，用户ID: {}", savedUser.getId());
        } catch (Exception e) {
            logger.error("对话表创建失败，用户ID: {}, 错误: {}", savedUser.getId(), e.getMessage());
        }

        // 6. 返回注册结果
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO loginUser(String username, String password) {
        // 1. 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("登录失败，用户不存在: " + username));

        // 2. 密码验证（生产环境需加密比对）
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("登录失败，密码错误");
        }
        logger.info("用户登录成功，ID: {}", user.getId());

        // 3. 检查对话表状态（核心逻辑）
        if (dialogueRepository.findByUserId(user.getId()).isEmpty()) {
            try {
                createDialogueForUser(user.getId());
                logger.info("登录时为用户创建对话表，用户ID: {}", user.getId());
            } catch (Exception e) {
                logger.error("登录时对话表创建失败，用户ID: {}, 错误: {}", user.getId(), e.getMessage());
            }
        }

        // 4. 返回登录结果
        return convertToDTO(user);
    }

    @Override
    public DialogueDTO createDialogueForUser(Long userId) {
        logger.info("开始为用户创建对话表，用户ID: {}", userId);

        // 1. 构建对话实体
        Dialogue dialogue = new Dialogue();
        User userRef = new User();
        userRef.setId(userId);
        dialogue.setUser(userRef);

        // 2. 设置默认属性
        dialogue.setMode("CHATBOX");       // 默认聊天模式
        dialogue.setSummary("");           // 初始无对话总结
        dialogue.setActive(true);          // 标记为活跃状态

        // 3. 持久化对话
        Dialogue savedDialogue = dialogueRepository.save(dialogue);
        logger.info("对话表创建完成，ID: {}", savedDialogue.getId());

        // 4. 转换为DTO返回
        return convertDialogueToDTO(savedDialogue);
    }

    /**
     * 实体转DTO（用户对象）
     * @param user 数据库实体
     * @return 前端传输对象
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setSignature(user.getSignature());
        dto.setAge(user.getAge());
        dto.setRegistertime(user.getRegistertime());

        // 关联对象ID映射（避免循环引用）
        if (user.getEmotionLogs() != null) {
            dto.setEmotionLogIds(user.getEmotionLogs().stream()
                    .map(log -> log.getId())
                    .collect(Collectors.toList()));
        }
        if (user.getDiaries() != null) {
            dto.setDiaryIds(user.getDiaries().stream()
                    .map(diary -> diary.getId())
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    /**
     * DTO转实体（用户对象）
     * @param dto 前端传输对象
     * @return 数据库实体
     */
    private User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setAvatar(dto.getAvatar());
        user.setGender(dto.getGender());
        user.setPassword(dto.getPassword());  // 注意：生产环境需加密存储
        user.setSignature(dto.getSignature());
        user.setAge(dto.getAge());
        user.setRegistertime(dto.getRegistertime());
        return user;
    }

    /**
     * 对话实体转DTO
     * @param dialogue 对话实体
     * @return 对话传输对象
     */
    private DialogueDTO convertDialogueToDTO(Dialogue dialogue) {
        DialogueDTO dto = new DialogueDTO();
        dto.setId(dialogue.getId());
        dto.setUserId(dialogue.getUser().getId());
        dto.setMode(dialogue.getMode());
        dto.setSummary(dialogue.getSummary());
        dto.setActive(dialogue.getActive());
        dto.setCreateTime(dialogue.getCreateTime());
        dto.setUpdateTime(dialogue.getUpdateTime());
        return dto;
    }
    @Override
    public List<User> getAllUsers() {
        // 调用 UserRepository 的 findAll 方法获取所有用户信息
        return userRepository.findAll();
    }
}