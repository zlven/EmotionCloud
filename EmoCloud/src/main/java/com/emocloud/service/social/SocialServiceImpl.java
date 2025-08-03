package com.emocloud.service.social;

import com.emocloud.dto.Social.CommentDTO;
import com.emocloud.dto.Social.SocialPostDTO;
import com.emocloud.dto.Social.SocialPostImageDTO;
import com.emocloud.model.User;
import com.emocloud.model.social.Comment;
import com.emocloud.model.social.PostImage;
import com.emocloud.model.social.SocialPost;
import com.emocloud.repository.social.CommentRepository;
import com.emocloud.repository.social.SocialPostImageRepository;
import com.emocloud.repository.social.SocialPostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
/**
 * 社交模块服务实现类，处理社交帖子和评论的业务逻辑
 * 包含帖子和评论的增删改查、数据转换等核心功能
 */
@Service
@Transactional
public class SocialServiceImpl implements SocialService {
    @Autowired
    private SocialPostImageRepository postImageRepository;
    @Autowired
    private SocialPostRepository postRepository; // 帖子数据访问层注入
    @Autowired
    private CommentRepository commentRepository; // 评论数据访问层注入
    @Value("${image.upload-dir:uploads/images}")
    private  String uploadPath;
    @Autowired
    private LocalFileStorageUtil localFileStorageUtil;

    // === 社交帖子(SocialPost)业务方法实现 ===
    /**
     * 创建新社交帖子
     * @param postDTO 帖子DTO对象
     * @return 转换后的帖子DTO（包含数据库生成的ID）
     */
    @Override
    public SocialPostDTO createPost(SocialPostDTO postDTO) {
        System.out.println(postDTO.toString());
        SocialPost post = convertToEntity(postDTO); // DTO转实体
        System.out.println(post.toString());
        SocialPost savedPost = postRepository.save(post); // 保存到数据库
        return convertToDTO(savedPost); // 实体转DTO返回
    }

    /**
     * 根据ID查询帖子
     * @param postId 帖子ID
     * @return 帖子DTO对象（Optional包装，可能为空）
     */
    @Override
    public Optional<SocialPostDTO> getPostById(Long postId) {
        return postRepository.findById(postId)
                .map(this::convertToDTO); // 查找到实体后转换为DTO
    }

    /**
     * 更新帖子信息
     * @param postId 帖子ID
     * @param postDTO 新的帖子DTO数据
     * @return 更新后的帖子DTO
     */
    @Override
    public SocialPostDTO updatePost(Long postId, SocialPostDTO postDTO) {
        return postRepository.findById(postId)
                .map(existingPost -> {
                    // 更新帖子内容（标题、正文、情绪标签等）
                    existingPost.setTitle(postDTO.getTitle());
                    existingPost.setContents(postDTO.getContents());
                    existingPost.setMoodTag(postDTO.getMoodTag());
                    // 保存更新并转换为DTO返回
                    SocialPost updatedPost = postRepository.save(existingPost);
                    return convertToDTO(updatedPost);
                })
                .orElseThrow(() -> new IllegalArgumentException("帖子不存在: " + postId));
    }

    /**
     * 删除指定ID的帖子
     * @param postId 帖子ID
     */
    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId); // 直接调用JPA删除方法
    }

    /**
     * 根据用户ID查询其发布的帖子（带分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页的帖子DTO列表
     */
    @Override
    public Page<SocialPostDTO> getPostsByUser(Long userId, Pageable pageable) {
        Page<SocialPost> posts = postRepository.findByPublisherId(userId, pageable);
        // 实体列表转DTO列表，并封装为分页对象
        List<SocialPostDTO> postDTOs = posts.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(postDTOs, pageable, posts.getTotalElements());
    }

    /**
     * 查询所有帖子（带分页）
     * @param pageable 分页参数
     * @return 分页的帖子DTO列表
     */
    @Override
    public Page<SocialPostDTO> getAllPosts(Pageable pageable) {
        // 打印分页参数，确认接收正常
        System.out.println("Pageable: " + pageable);
        try {
            // 查询数据库
            Page<SocialPost> posts = postRepository.findAll(pageable);
            // 打印原始数据（确认有内容）
            System.out.println("Posts from DB: " + posts.getContent());

            // 安全转换为DTO列表（过滤null值）
            List<SocialPostDTO> postDTOs = posts.getContent().stream()
                    .filter(Objects::nonNull) // 过滤可能的null值
                    .map(this::convertToDTO)
                    .filter(Objects::nonNull) // 过滤转换失败的null值
                    .collect(Collectors.toList());

            // 打印转换后的DTO（确认转换成功）
            System.out.println("Converted DTOs: " + postDTOs);

            // 返回分页结果
            return new PageImpl<>(postDTOs, pageable, posts.getTotalElements());
        }
        catch (Exception e) {
            System.err.println("查询帖子时发生异常: " + e.getMessage());
            e.printStackTrace(); // 打印完整堆栈信息
            throw e; // 重新抛出异常，保持原有行为
        }
    }    // === 评论(Comment)业务方法实现 ===
    /**
     * 创建新评论
     * @param commentDTO 评论DTO对象
     * @return 转换后的评论DTO（包含数据库生成的ID）
     */
    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        System.out.println("entry the service layer"+commentDTO.toString());
        Comment comment = convertToEntity(commentDTO); // DTO转实体
        System.out.println(comment.toString());
        Comment savedComment = commentRepository.save(comment); // 保存到数据库
        System.out.println("success");
        return convertToDTO(savedComment); // 实体转DTO返回
    }

    /**
     * 根据ID查询评论
     * @param commentId 评论ID
     * @return 评论DTO对象（Optional包装，可能为空）
     */
    @Override
    public Optional<CommentDTO> getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .map(this::convertToDTO); // 查找到实体后转换为DTO
    }

    /**
     * 更新评论内容
     * @param commentId 评论ID
     * @param commentDTO 新的评论DTO数据
     * @return 更新后的评论DTO
     */
    @Override
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        return commentRepository.findById(commentId)
                .map(existingComment -> {
                    // 更新评论内容
                    existingComment.setContent(commentDTO.getContent());
                    // 保存更新并转换为DTO返回
                    Comment updatedComment = commentRepository.save(existingComment);
                    return convertToDTO(updatedComment);
                })
                .orElseThrow(() -> new IllegalArgumentException("评论不存在: " + commentId));
    }

    /**
     * 删除指定ID的评论
     * @param commentId 评论ID
     */
    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId); // 直接调用JPA删除方法
    }

    /**
     * 根据帖子ID查询评论（带分页）
     * @param postId 帖子ID
     * @param pageable 分页参数
     * @return 分页的评论DTO列表
     */
    @Override
    public Page<CommentDTO> getCommentsByPost(Long postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        // 实体列表转DTO列表，并封装为分页对象
        List<CommentDTO> commentDTOs = comments.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(commentDTOs, pageable, comments.getTotalElements());
    }
    /**
     * 根据postid查询评论；返回链表形式；
     *
     */
    public List<CommentDTO> getCommentsByPostId(Long postId){
        // 1. 从数据库获取评论列表（按创建时间降序）
        List<Comment> comments = commentRepository.findByPostId(postId);

        // 2. 转换为DTO列表
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreateTime).reversed()) // 确保最新在前
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // === 数据转换方法实现 ===
    /**
     * 实体类(SocialPost)转DTO类(SocialPostDTO)
     * @param post 帖子实体对象
     * @return 帖子DTO对象
     */
    @Override
    public SocialPostDTO convertToDTO(SocialPost post) {
        SocialPostDTO dto = new SocialPostDTO();
        dto.setPostid(post.getId());             // 帖子ID
        dto.setTitle(post.getTitle());           // 标题
        dto.setContents(post.getContents());     // 内容
        dto.setCrateTime(post.getCreateTime());   // 创建时间
        dto.setUpdateTime(post.getUpdateTime());  // 更新时间
        dto.setCommentCount(post.getCommentCount()); // 评论数
        dto.setCollectCount(post.getCollectCount()); // 收藏数
        dto.setLikeCount(post.getLikeCount());     // 点赞数
        dto.setMoodTag(post.getMoodTag());         // 情绪标签
        dto.setShareCount(post.getShareCount());   // 分享数
        dto.setPublisherId(post.getPublisher().getId()); // 发布者ID
        dto.setPublisherAvatar(post.getPublisherAvatar()); // 发布者头像
        return dto;
    }

    /**
     * DTO类(SocialPostDTO)转实体类(SocialPost)
     * @param postDTO 帖子DTO对象
     * @return 帖子实体对象
     */
    @Override
    public SocialPost convertToEntity(SocialPostDTO postDTO) {
        SocialPost post = new SocialPost();
        post.setId(postDTO.getPostid());
        post.setTitle(postDTO.getTitle());
        post.setContents(postDTO.getContents());
        post.setCreateTime(String.valueOf(LocalDateTime.now()));//获取当时时间
        post.setUpdateTime(String.valueOf(LocalDateTime.now()));
        post.setCommentCount(postDTO.getCommentCount());
        post.setCollectCount(postDTO.getCollectCount());
        post.setLikeCount(postDTO.getLikeCount());
        post.setMoodTag(postDTO.getMoodTag());
        post.setShareCount(postDTO.getShareCount());

        // 修复：初始化 Publisher 对象
        User publisher = new User(); // 假设 Publisher 类型为 User
        publisher.setId(postDTO.getPublisherId());
        publisher.setAvatar(postDTO.getPublisherAvatar());
        post.setPublisher(publisher); // 设置完整的 Publisher 对象

        return post;
    }

    /**
     * 实体类(Comment)转DTO类(CommentDTO)
     * @param comment 评论实体对象
     * @return 评论DTO对象
     */
    @Override
    public CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());               // 评论ID
        dto.setContent(comment.getContent());     // 评论内容
        dto.setCreateTime(comment.getCreateTime()); // 创建时间
        dto.setLikesCount(comment.getLikesCount()); // 点赞数
        dto.setDeleted(comment.getDeleted());     // 删除状态
        dto.setPostId(comment.getPost().getId());  // 所属帖子ID
        dto.setAuthorId(comment.getAuthor().getId()); // 评论者ID
        dto.setParentId(comment.getParent().getId()); // 父评论ID（用于回复）
        return dto;
    }

    /**
     * DTO类(CommentDTO)转实体类(Comment)
     * @param commentDTO 评论DTO对象
     * @return 评论实体对象
     */
    @Override
    public Comment convertToEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setId(commentDTO.getId());               // 评论ID
        comment.setContent(commentDTO.getContent());     // 评论内容
        comment.setCreateTime(LocalDateTime.now());      // 设置为当前时间
        comment.setLikesCount(0); // 点赞数
        comment.setDeleted(false);     // 删除状态

        // 正确处理关联实体（推荐方式）
        if (commentDTO.getPostId() != null) {
            SocialPost post = new  SocialPost();
            post.setId(commentDTO.getPostId());
            comment.setPost(post);
        }

        if (commentDTO.getAuthorId() != null) {
            User author = new User();
            author.setId(commentDTO.getAuthorId());
            comment.setAuthor(author);
        }

        if (commentDTO.getParentId() != null) {
            Comment parent = new Comment();
            parent.setId(commentDTO.getParentId());
            comment.setParent(parent);
        }

        return comment;
    }

    @Override
    @Transactional
    public SocialPostImageDTO uploadPostImage(Long postId, MultipartFile file, Integer sortOrder) throws IOException {
        // 1. 验证帖子是否存在
        SocialPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("帖子不存在，ID: " + postId));

        // 2. 保存图片到本地文件系统
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String uniqueFileName = UUID.randomUUID() + "." + fileType;
        originalFilename=uniqueFileName;
        String imageUrl = localFileStorageUtil.storeFile(file, originalFilename); // 使用本地存储工具类
        // 3. 创建图片实体
        PostImage postImage = new PostImage(
                imageUrl,          // 存储相对路径
                originalFilename,
                fileType,
                file.getSize(),
                post,
                sortOrder
        );

        // 4. 保存到数据库
        PostImage savedImage = postImageRepository.save(postImage);

        // 5. 转换为DTO并返回
        return convertToDTO(savedImage);
    }
    @Override
    @Transactional
    public List<SocialPostImageDTO> uploadPostImages(Long postId, List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            return new ArrayList<>();
        }

        SocialPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("帖子不存在，ID: " + postId));

        List<SocialPostImageDTO> result = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                continue;
            }

            // 保存图片到本地
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String uniqueFileName = UUID.randomUUID() + "." + fileType;
            String imageUrl = localFileStorageUtil.storeFile(file, uniqueFileName);

            // 创建图片实体
            PostImage postImage = new PostImage(
                    imageUrl,
                    originalFilename,
                    fileType,
                    file.getSize(),
                    post,
                    i + 1 // 使用文件索引作为排序序号
            );

            // 保存到数据库
            PostImage savedImage = postImageRepository.save(postImage);

            // 添加到结果列表
            result.add(convertToDTO(savedImage));
        }

        return result;
    }

    @Override
    public List<SocialPostImageDTO> getImagesByPostId(Long postId) {
        System.out.println("enter getImagesByPostId"+postId);
        List<PostImage> images = postImageRepository.findByPostId(postId);
        System.out.println("getImages");
        return images.stream()
                .map(this::convertToDTO)
                .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
                .toList();
    }

    @Override
    @Transactional
    public void deleteImagesByPostId(Long postId) {
        // 先从数据库查询所有图片
        List<PostImage> images = postImageRepository.findByPostId(postId);

        // 删除文件系统中的图片
        for (PostImage image : images) {
            localFileStorageUtil.deleteFile(image.getImageUrl());
        }

        // 删除数据库记录
        postImageRepository.deleteByPostId(postId);
    }
    @Override
    @Transactional(readOnly = true)
    public List<SocialPostImageDTO> getPostImages(Long postId){
        // 1. 验证帖子是否存在
        SocialPost post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("帖子不存在，ID: " + postId));
        List<PostImage> images = postImageRepository.findByPostId(postId);
        return images.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    /**
     * 实体转DTO
     */
    private SocialPostImageDTO convertToDTO(PostImage entity) {
        SocialPostImageDTO dto = new SocialPostImageDTO();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setFileName(entity.getFileName());
        dto.setFileType(entity.getFileType());
        dto.setFileSize(entity.getFileSize());
        dto.setUploadTime(entity.getUploadTime());
        dto.setSortOrder(entity.getSortOrder());
        dto.setPostId(entity.getPost().getId());
        return dto;
    }
}