
package com.emocloud.controller;

import com.emocloud.dto.UserDTO;
import com.emocloud.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器，处理用户注册和登录请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口
     * @param userDTO 注册用户信息DTO
     * @return 注册成功的用户信息和状态码
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    /**
     * 用户登录接口
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息和状态码
     */
    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestParam String username,
                                             @RequestParam String password) {
        UserDTO userDTO = userService.loginUser(username, password);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
}