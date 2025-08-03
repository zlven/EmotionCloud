package com.emocloud.repository;

import com.emocloud.model.EmotionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// EmotionTagRepository 用于操作 EmotionTag 实体的数据库交互
@Repository
public interface EmotionTagRepository extends JpaRepository<EmotionTag, Long> {
    /**
     * 根据描述查找情绪标签
     * @param description 情绪标签的描述
     * @return 匹配的情绪标签，如果未找到则返回 Optional.empty()
     */
    Optional<EmotionTag> findByDescription(String description);
}