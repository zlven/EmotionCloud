package com.emocloud.repository.companion;

import com.emocloud.model.companion.Dialogue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// DialogueRepository 用于操作 Dialogue 实体的数据库交互
@Repository
public interface DialogueRepository extends JpaRepository<Dialogue, Long> {
    /**
     * 根据用户 ID 查找对话
     *
     * @param userId 用户的 ID
     * @return 匹配的对话列表
     */
    Page<Dialogue> findByUserId(Long userId, Pageable pageable);
    /**
     * 根据用户 ID 查找对话
     *
     * @param userId 用户的 ID
     * @return 匹配的对话列表
     */
    List<Dialogue> findByUserId(Long userId);

    /**
     * 根据模式查找对话
     * @param mode 对话的模式
     * @return 匹配的对话列表
     */
    List<Dialogue> findByMode(String mode);

    /**
     * 根据 ID 查找对话
     * @param id 对话的 ID
     * @return 匹配的对话，如果未找到则返回 Optional.empty()
     */
    Optional<Dialogue> findById(Long id);
}