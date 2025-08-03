package com.emocloud.repository;

import com.emocloud.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// VideoRepository 用于操作 Video 实体的数据库交互
@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    /**
     * 根据 ID 查找视频
     * @param id 视频的 ID
     * @return 匹配的视频，如果未找到则返回 Optional.empty()
     */
    Optional<Video> findById(Long id);
}