package com.emocloud.repository;

import com.emocloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository 用于操作 User 实体的数据库交互。
 * 它继承自 JpaRepository，这是 Spring Data JPA 提供的一个接口，
 * 提供了基本的 CRUD 操作和分页、排序等功能。
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根据用户名查找用户。
     *
     * @param username 用户名
     * @return 匹配的用户，如果未找到则返回 Optional.empty()
     */
    Optional<User> findByUsername(String username);
    // 该方法可获取所有用户信息
    List<User> findAll();



}