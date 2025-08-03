package com.emocloud.repository;

import com.emocloud.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// MusicRepository 用于操作 Music 实体的数据库交互
@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    /**
     * 根据歌曲名称查找音乐
     * @param name 歌曲名称
     * @return 匹配的音乐列表
     */
    List<Music> findByName(String name);

    /**
     * 根据歌手名称查找音乐
     * @param artist 歌手名称
     * @return 匹配的音乐列表
     */
    List<Music> findByArtist(String artist);

    /**
     * 根据 ID 查找音乐
     * @param id 音乐的 ID
     * @return 匹配的音乐，如果未找到则返回 Optional.empty()
     */
    Optional<Music> findById(Long id);
}