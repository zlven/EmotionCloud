package com.emocloud.controller;

import com.emocloud.dto.UserDTO;
import com.emocloud.model.User;
import com.emocloud.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器，处理用户信息的查询和更新
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(convertToDTO(user), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDTO 用户信息DTO
     * @return 更新后的用户信息DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody UserDTO userDTO) {
        // 确保DTO中的ID与路径参数一致
        userDTO.setId(id);

        // 先检查用户是否存在
        if (!userService.getUserById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User updatedUser = userService.updateUser(convertToEntity(userDTO));
        return new ResponseEntity<>(convertToDTO(updatedUser), HttpStatus.OK);
    }

    /**
     * 将User实体转换为UserDTO（简化版）
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
        return dto;
    }

    /**
     * 将UserDTO转换为User实体（简化版）
     */
    private User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setAvatar(dto.getAvatar());
        user.setGender(dto.getGender());
        user.setSignature(dto.getSignature());
        user.setAge(dto.getAge());
        user.setRegistertime(dto.getRegistertime());
        return user;
    }
}