package com.emocloud.repository.companion;

import com.emocloud.model.companion.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// MessageRepository 用于操作 Message 实体的数据库交互
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    /**
     * 根据对话 ID 查找消息
     *
     * @param dialogueId 对话的 ID
     * @return 匹配的消息列表
     */
    Page<Message> findByDialogueId(Long dialogueId, Pageable pageable);

    /**
     * 根据内容类型查找消息
     * @param contentType 消息的内容类型
     * @return 匹配的消息列表
     */
    List<Message> findByContentType(String contentType);

    /**
     * 根据 ID 查找消息
     * @param id 消息的 ID
     * @return 匹配的消息，如果未找到则返回 Optional.empty()
     */
    Optional<Message> findById(Long id);
}