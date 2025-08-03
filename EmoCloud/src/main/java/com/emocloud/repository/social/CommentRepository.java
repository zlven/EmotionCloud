package com.emocloud.repository.social;

import com.emocloud.model.social.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// CommentRepository 用于操作 Comment 实体的数据库交互
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * 根据社交帖子 ID 查找评论
     *
     * @param postId 社交帖子的 ID
     * @return 匹配的评论列表
     */
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    List<Comment> findByPostId(Long postId);

    /**
     * 根据评论者 ID 查找评论
     * @param authorId 评论者的 ID
     * @return 匹配的评论列表
     */
    List<Comment> findByAuthorId(Long authorId);

    /**
     * 根据 ID 查找评论
     * @param id 评论的 ID
     * @return 匹配的评论，如果未找到则返回 Optional.empty()
     */
    Optional<Comment> findById(Long id);
}