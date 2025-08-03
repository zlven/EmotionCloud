package com.emocloud.repository.social;

import com.emocloud.model.social.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社交帖子图片的数据库操作接口
 */
@Repository
public interface SocialPostImageRepository extends JpaRepository<PostImage, Long> {

    /**
     * 根据帖子ID查询所有图片
     * @param postId 帖子ID
     * @return 图片列表
     */
    List<PostImage> findByPostId(Long postId);

    /**
     * 根据帖子ID和排序序号查询图片
     * @param postId 帖子ID
     * @param sortOrder 排序序号
     * @return 图片对象
     */
    PostImage findByPostIdAndSortOrder(Long postId, Integer sortOrder);

    /**
     * 根据帖子ID删除所有图片
     * @param postId 帖子ID
     */
    void deleteByPostId(Long postId);

    /**
     * 查询帖子的图片数量
     * @param postId 帖子ID
     * @return 图片数量
     */
    int countByPostId(Long postId);

    /**
     * 根据帖子ID和图片ID查询图片
     * @param postId 帖子ID
     * @param imageId 图片ID
     * @return 图片对象
     */
    PostImage findByPostIdAndId(Long postId, Long imageId);
}