package com.emocloud.repository.social;

import com.emocloud.model.social.SocialPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 社交帖子数据访问接口，提供对 SocialPost 实体的数据库操作
 */
@Repository
public interface SocialPostRepository extends JpaRepository<SocialPost, Long> {

    /**
     * 根据发布者ID和心情标签查询帖子列表
     * @param publisherId 发布者ID（对应 User 实体的主键）
     * @param moodTag 心情标签
     * @return 符合条件的帖子列表
     */
    List<SocialPost> findByPublisherIdAndMoodTag(Long publisherId, String moodTag);

    /**
     * 根据发布者ID和标题关键词查询帖子列表
     * @param publisherId 发布者ID
     * @param keyword 标题关键词（模糊匹配）
     * @return 符合条件的帖子列表
     */
    List<SocialPost> findByPublisherIdAndTitleContaining(Long publisherId, String keyword);

    /**
     * 根据发布者ID分页查询帖子列表
     * @param publisherId 发布者ID
     * @param pageable 分页参数
     * @return 分页后的帖子列表
     */
    Page<SocialPost> findByPublisherId(Long publisherId, Pageable pageable);

    /**
     * 根据帖子ID查询单个帖子
     * @param id 帖子ID
     * @return 帖子对象（Optional包装，可能为空）
     */
    @Override
    Optional<SocialPost> findById(Long id);

}