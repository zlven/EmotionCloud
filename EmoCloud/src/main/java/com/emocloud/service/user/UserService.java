package com.emocloud.service.user;

import com.emocloud.dto.EmotionCompanionDto.DialogueDTO;
import com.emocloud.dto.UserDTO;
import com.emocloud.model.User;

import java.util.List;
import java.util.Optional;

/**
 * UserService 是一个接口，定义了用户服务的基本操作。
 * 这些操作包括获取用户信息、更新用户信息、用户注册、用户登录以及为用户创建对话。
 */
public interface UserService {
    /**
     * 根据用户 ID 获取用户信息。
     *
     * @param id 用户 ID
     * @return 用户信息，如果未找到则返回 Optional.empty()
     */
    Optional<User> getUserById(Long id);

    /**
     * 更新用户信息。
     *
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);

    /**
     * 用户注册功能。
     *
     * @param userDTO 注册用户信息 DTO
     * @return 注册成功的用户信息 DTO
     */
    UserDTO registerUser(UserDTO userDTO);

    /**
     * 用户登录功能。
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息 DTO
     */
    UserDTO loginUser(String username, String password);

    /**
     * 用户登录后自动创建对话。
     *
     * @param userId 用户 ID
     * @return 创建成功后的对话 DTO
     */
    DialogueDTO createDialogueForUser(Long userId);
    // 声明获取所有用户信息的方法
    List<User> getAllUsers();
}