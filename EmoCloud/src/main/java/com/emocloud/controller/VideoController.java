package com.emocloud.controller;

import com.emocloud.dto.VideoDTO;
import com.emocloud.model.Video;
import com.emocloud.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 视频控制器，处理与视频相关的 HTTP 请求
 */
@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;


    /**
     * 根据 ID 查找视频
     * @param id 视频 ID
     * @return 匹配的视频响应
     */
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isPresent()) {
            return new ResponseEntity<>(video.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Video not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 保存视频信息
     * @param videoDTO 视频信息 DTO
     * @return 保存后的视频信息响应
     */
    @PostMapping("/save")
    public ResponseEntity<Video> saveVideo(@RequestBody VideoDTO videoDTO) {
        Video video = new Video();
        // 这里可以根据需要将 videoDTO 的属性复制到 video
        Video savedVideo = videoRepository.save(video);
        return new ResponseEntity<>(savedVideo, HttpStatus.CREATED);
    }
}