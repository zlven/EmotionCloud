package com.emocloud.service.social;

import com.emocloud.dto.Social.CommentDTO;
import com.emocloud.dto.Social.SocialPostDTO;
import com.emocloud.dto.Social.SocialPostImageDTO;
import com.emocloud.model.social.Comment;
import com.emocloud.model.social.SocialPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
/**
 * 社交模块服务接口，提供社交帖子和评论的管理功能
 */
public interface SocialService {
    // === SocialPost 相关方法 ===
    /**
     * 创建社交帖子
     * @param postDTO 帖子请求DTO
     * @return 创建后的帖子DTO
     */
    SocialPostDTO createPost(SocialPostDTO postDTO);

    /**
     * 根据ID获取社交帖子
     * @param postId 帖子ID
     * @return 帖子DTO（Optional可能为空）
     */
    Optional<SocialPostDTO> getPostById(Long postId);

    /**
     * 更新社交帖子
     * @param postId 帖子ID
     * @param postDTO 更新内容
     * @return 更新后的帖子DTO
     */
    SocialPostDTO updatePost(Long postId, SocialPostDTO postDTO);

    /**
     * 删除社交帖子
     * @param postId 帖子ID
     */
    void deletePost(Long postId);

    /**
     * 获取用户发布的所有帖子
     * @param userId 用户ID
     * @param pageable 分页信息
     * @return 帖子分页列表
     */
    Page<SocialPostDTO> getPostsByUser(Long userId, Pageable pageable);

    /**
     * 获取所有帖子（分页）
     * @param pageable 分页信息
     * @return 帖子分页列表
     */
    Page<SocialPostDTO> getAllPosts(Pageable pageable);

    // === Comment 相关方法 ===
    /**
     * 创建评论
     * @param commentDTO 评论请求DTO
     * @return 创建后的评论DTO
     */
    CommentDTO createComment(CommentDTO commentDTO);

    /**
     * 根据ID获取评论
     * @param commentId 评论ID
     * @return 评论DTO（Optional可能为空）
     */
    Optional<CommentDTO> getCommentById(Long commentId);

    /**
     * 更新评论
     * @param commentId 评论ID
     * @param commentDTO 更新内容
     * @return 更新后的评论DTO
     */
    CommentDTO updateComment(Long commentId, CommentDTO commentDTO);

    /**
     * 删除评论
     * @param commentId 评论ID
     */
    void deleteComment(Long commentId);

    /**
     * 获取帖子下的所有评论
     * @param postId 帖子ID
     * @param pageable 分页信息
     * @return 评论分页列表
     */
    Page<CommentDTO> getCommentsByPost(Long postId, Pageable pageable);

    /**
     * 上传帖子文件
     * @param postId 帖子ID
     * @param file 图片文件
     * @param sortOrder 排序序号
     * @return 上传成功的图片DTO
     * @throws IOException 文件处理异常
     */
    SocialPostImageDTO uploadPostImage(Long postId, MultipartFile file, Integer sortOrder) throws IOException;

    /**
     * 批量上传文件；
     * @param postId 帖子ID
     * @param files 图片文件列表
     * @return 上传成功的图片DTO列表
     * @throws IOException 文件处理异常
     */
    List<SocialPostImageDTO> uploadPostImages(Long postId, List<MultipartFile> files) throws IOException;

    /**
     * 根据帖子ID获取所有图片
     * @param postId 帖子ID
     * @return 图片DTO列表
     */
    List<SocialPostImageDTO> getImagesByPostId(Long postId);

    /**
     * 删除帖子的所有图片
     * @param postId 帖子ID
     */
    void deleteImagesByPostId(Long postId);
    // === 辅助方法（Model与DTO转换） ===
    SocialPostDTO convertToDTO(SocialPost post);
    SocialPost convertToEntity(SocialPostDTO postDTO);
    CommentDTO convertToDTO(Comment comment);
    Comment convertToEntity(CommentDTO commentDTO);

    List<SocialPostImageDTO> getPostImages(Long postId);


    List<CommentDTO> getCommentsByPostId(Long postId);
}