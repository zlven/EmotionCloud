package com.emocloud.controller;

import com.emocloud.dto.Social.CommentDTO;
import com.emocloud.model.social.Comment;
import com.emocloud.repository.social.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 评论控制器，处理与评论相关的 HTTP 请求
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * 根据社交帖子 ID 查找评论
     * @param postId 社交帖子 ID
     * @return 匹配的评论列表
     */
    @GetMapping("/find-by-post-id/{postId}")
    public ResponseEntity<List<Comment>> findByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * 根据评论者 ID 查找评论
     * @param authorId 评论者 ID
     * @return 匹配的评论列表
     */
    @GetMapping("/find-by-commenter-id/{authorId}")
    public ResponseEntity<List<Comment>> findByAuthorId(@PathVariable Long authorId) {
        List<Comment> comments = commentRepository.findByAuthorId(authorId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * 根据 ID 查找评论
     * @param id 评论 ID
     * @return 匹配的评论响应
     */
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            return new ResponseEntity<>(comment.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 保存评论信息
     * @param commentDTO 评论信息 DTO
     * @return 保存后的评论信息响应
     */
    @PostMapping("/save")
    public ResponseEntity<Comment> saveComment(@RequestBody CommentDTO commentDTO) {
        Comment comment = new Comment();
        // 这里可以根据需要将 commentDTO 的属性复制到 comment
        Comment savedComment = commentRepository.save(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }
}