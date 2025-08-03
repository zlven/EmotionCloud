package com.emocloud.repository.companion;

import com.emocloud.model.companion.SceneMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// SceneModeRepository 用于操作 SceneMode 实体的数据库交互
@Repository
public interface SceneModeRepository extends JpaRepository<SceneMode, Long> {
    /**
     * 根据模式名称查找场景模式
     * @param sceneName 场景模式的名称
     * @return 匹配的场景模式，如果未找到则返回 Optional.empty()
     */
    Optional<SceneMode> findBySceneName(String sceneName);
}