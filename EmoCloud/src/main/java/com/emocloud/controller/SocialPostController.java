package com.emocloud.controller;

import com.emocloud.dto.Social.CommentDTO;
import com.emocloud.dto.Social.SocialPostDTO;
import com.emocloud.dto.Social.SocialPostImageDTO;
import com.emocloud.service.social.SocialService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 社交模块控制器，提供帖子和评论的 RESTful API
 */
@RestController
@RequestMapping("/social")
public class SocialPostController {
    private static final Logger logger = LoggerFactory.getLogger(SocialPostController.class);
    @Autowired
    private SocialService socialService;

    // === 社交帖子(SocialPost)相关接口 ===

    /**
     * 创建新的社交帖子
     * @param postDTO 帖子数据（JSON格式）
     * @return 创建成功的帖子DTO，HTTP状态码201(CREATED)
     */
    @PostMapping("/posts")
    public ResponseEntity<SocialPostDTO> createPost(@RequestBody SocialPostDTO postDTO) {
        System.out.println("postDTO: " + postDTO.toString());
        SocialPostDTO createdPost = socialService.createPost(postDTO);
        System.out.println("createdPost: " + createdPost.toString());
        System.out.println("保存的帖子ID: " + createdPost.getPostid()); // 关键日志
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }



    /**
     * 更新帖子信息
     * @param postId 帖子ID
     * @param postDTO 更新的帖子数据（JSON格式）
     * @return 更新后的帖子DTO，HTTP状态码200(OK)；帖子不存在则返回404(NOT_FOUND)
     */
    @PutMapping("/posts/{postId}")
    public ResponseEntity<SocialPostDTO> updatePost(@PathVariable Long postId, @RequestBody SocialPostDTO postDTO) {
        System.out.println("postDTO: " + postDTO.toString());
        try {
            SocialPostDTO updatedPost = socialService.updatePost(postId, postDTO);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @return HTTP状态码204(NO_CONTENT)
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        socialService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 获取指定用户发布的所有帖子（分页）
     * @param userId 用户ID
     * @param pageable 分页参数（page=页码，size=每页数量，sort=排序字段）
     * @return 帖子分页列表，HTTP状态码200(OK)
     */
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<Page<SocialPostDTO>> getPostsByUser(@PathVariable Long userId, Pageable pageable) {
        System.out.println("enrty ,pageable: " + pageable.toString());
        Page<SocialPostDTO> posts = socialService.getPostsByUser(userId, pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * 获取所有帖子（分页）
     * @param pageable 分页参数（page=页码，size=每页数量，sort=排序字段）
     * @return 帖子分页列表，HTTP状态码200(OK)
     */
    @GetMapping("/getposts")
    public ResponseEntity<Page<SocialPostDTO>> getAllPosts(Pageable pageable) {
        System.out.println("enrty ,pageable: " + pageable.toString());
        Page<SocialPostDTO> posts = socialService.getAllPosts(pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
    /**
     * 根据postid获取帖子详情
     * @param postId 帖子ID
     * @return 帖子详情，HTTP状态码200(OK)；如果不存在返回404(NOT_FOUND)
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<SocialPostDTO> getPostById(@PathVariable Long postId) {
        logger.info("获取帖子详情，postId: {}", postId);
        return socialService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * 获取帖子的图片列表
     * @param postId 帖子ID
     * @return 图片列表
     */
    @GetMapping("/posts/{postId}/images")
    @CrossOrigin // 如果存在跨域问题添加这个
    public ResponseEntity<List<SocialPostImageDTO>> getPostImages(@PathVariable Long postId) {
        try {
            System.out.println("enrty ,postId: " + postId);
            List<SocialPostImageDTO> images = socialService.getPostImages(postId);
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // === 上传图片（文件）接口；

    // === 评论(Comment)相关接口 ===

    /**
     * 创建新评论
     * @param commentDTO 评论数据（JSON格式）
     * @return 创建成功的评论DTO，HTTP状态码201(CREATED)
     */
    @PostMapping("/comments")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        System.out.println("entry the comment create,commentDTO: " + commentDTO.toString());
        CommentDTO createdComment = socialService.createComment(commentDTO);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }
    /**
     * 获取帖子评论列表
     * @param postId 帖子ID
     * @return 评论列表
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(
            @PathVariable Long postId) {
        System.out.println("enrty ,postId: " + postId);
        List<CommentDTO> comments = socialService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
    /**
     * 根据ID获取评论详情
     * @param commentId 评论ID
     * @return 评论DTO（存在时），HTTP状态码200(OK)；不存在则返回404(NOT_FOUND)
     */
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long commentId) {
        Optional<CommentDTO> comment = socialService.getCommentById(commentId);
        return comment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 更新评论信息
     * @param commentId 评论ID
     * @param commentDTO 更新的评论数据（JSON格式）
     * @return 更新后的评论DTO，HTTP状态码200(OK)；评论不存在则返回404(NOT_FOUND)
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO) {
        try {
            CommentDTO updatedComment = socialService.updateComment(commentId, commentDTO);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return HTTP状态码204(NO_CONTENT)
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        socialService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 获取指定帖子下的所有评论（分页）
     * @param postId 帖子ID
     * @param pageable 分页参数（page=页码，size=每页数量，sort=排序字段）
     * @return 评论分页列表，HTTP状态码200(OK)
     */
    @GetMapping
    public ResponseEntity<Page<CommentDTO>> getCommentsByPost(@PathVariable Long postId, Pageable pageable) {
        Page<CommentDTO> comments = socialService.getCommentsByPost(postId, pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    /**
     * 上传单张图片到指定帖子
     * @param postId 帖子ID
     * @param file 图片文件
     * @param sortOrder 排序序号（可选）
     * @return 上传成功的图片DTO
     */
    @PostMapping(value = "/posts/{postId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SocialPostImageDTO> uploadImage(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder) {
        //判断是否进入该函数
        System.out.println("进入上传单张图片");

        try {
            SocialPostImageDTO imageDTO = socialService.uploadPostImage(postId, file, sortOrder);
            return new ResponseEntity<>(imageDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            logger.error("上传图片失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 批量上传图片到指定帖子
     * @param postId 帖子ID
     * @param files 图片文件列表
     * @return 上传成功的图片DTO列表
     */
    @PostMapping(value = "/posts/{postId}/images/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<SocialPostImageDTO>> uploadImages(
            @PathVariable Long postId,
            @RequestParam("files") List<MultipartFile> files) {
        //判断是否进入该函数
        System.out.println("进入上传多张图片"+postId);
        try {
            List<SocialPostImageDTO> imageDTOs = socialService.uploadPostImages(postId, files);
            System.out.println("<UNK>"+imageDTOs.size());
            return new ResponseEntity<>(imageDTOs, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            logger.error("批量上传图片失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/posts/{postId}/images/resources")
    public ResponseEntity<List<String>> getPostImageResources(
            @PathVariable Long postId,
            @Value("${image.upload-dir}") String uploadDir,
            HttpServletRequest request) {

        try {
            logger.info("获取帖子{}的图片资源，uploadDir={}", postId, uploadDir);
            System.out.println("<UNK>");
            // 打印uploadDir的绝对路径，帮助确认路径是否正确
            File uploadDirFile = new File(uploadDir);
            logger.info("上传目录绝对路径: {}", uploadDirFile.getAbsolutePath());
            System.out.println("上传目录绝对路径: "+uploadDirFile.getAbsolutePath());
            // 检查上传目录是否存在且可读
            if (!uploadDirFile.exists()) {
                logger.error("上传目录不存在: {}", uploadDir);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonList("上传目录不存在"));
            }

            if (!uploadDirFile.canRead()) {
                logger.error("没有权限读取上传目录: {}", uploadDir);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonList("没有权限读取上传目录"));
            }

            // 获取图片元数据
            List<SocialPostImageDTO> images = socialService.getPostImages(postId);
            logger.info("获取到{}张图片元数据", images != null ? images.size() : 0);
            System.out.println("获取到{}张图片元数据"+ images.size());
            if (images == null || images.isEmpty()) {
                logger.warn("帖子{}没有图片资源", postId);
                return ResponseEntity.ok(Collections.emptyList());
            }

            // 构建URL列表
            List<String> imageUrls = new ArrayList<>();
            for (SocialPostImageDTO image : images) {
                if (image == null) {
                    logger.error("图片元数据为空");
                    continue;
                }
                String fileName = image.getFileName();
                if (fileName == null || fileName.isEmpty()) {
                    logger.error("图片文件名缺失: {}", image);
                    continue;
                }

                Path imagePath = Paths.get(uploadDir).resolve(fileName).normalize();
                System.out.println("最后路径："+imagePath);

                // 检查文件是否存在且可读
                File imageFile = imagePath.toFile();
                if (!imageFile.exists()) {
                    logger.warn("图片文件不存在: {}", imagePath);
                    continue;
                }

                if (!imageFile.canRead()) {
                    logger.warn("没有权限读取图片文件: {}", imagePath);
                    continue;
                }

                // 构建基于当前请求上下文的URL
                String imageUrl = ServletUriComponentsBuilder.fromContextPath(request)
                        .path("/social/images/")
                        .path(fileName)
                        .toUriString();
                logger.info("最后的url路径{}", imageUrl);
                System.out.println("当前请求上下文的URL:"+imageUrl);
                imageUrls.add(imageUrl);
                logger.debug("图片URL构建成功: {}", imageUrl);
            }

            logger.info("成功构建{}个图片URL", imageUrls.size());

            // 打印最终返回的URL列表，便于调试
            if (imageUrls.isEmpty()) {
                logger.warn("最终返回的图片URL列表为空");
            } else {
                logger.info("返回的图片URL列表: {}", imageUrls);
            }
            return ResponseEntity.ok(imageUrls);
        } catch (Exception e) {
            logger.error("获取图片资源失败，postId={}", postId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList("获取图片资源失败: " + e.getMessage()));
        }
    }

    /**
     * 删除指定帖子的所有图片
     * @param postId 帖子ID
     * @return HTTP状态码
     */
    @DeleteMapping("/posts/{postId}/images")
    public ResponseEntity<Void> deleteImagesByPostId(@PathVariable Long postId) {
        try {
            socialService.deleteImagesByPostId(postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("删除图片失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private String determineContentType(String filename) {
        // 简单实现，根据扩展名判断
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".gif")) return "image/gif";
        if (filename.endsWith(".webp")) return "image/webp";
        return "image/jpeg"; // 默认
    }

}