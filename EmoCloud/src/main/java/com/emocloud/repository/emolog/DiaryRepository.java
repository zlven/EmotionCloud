package com.emocloud.repository.emolog;

import com.emocloud.model.emolog.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 情绪日记数据访问接口，继承 JpaRepository 提供基本的数据库操作
 */
@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    /**
     * 根据用户 ID 获取该用户的所有日记
     * @param userId 用户的 ID
     * @return 匹配的日记列表
     */
    List<Diary> findByUserId(Long userId);

    /**
     * 根据标题查找日记
     * @param title 日记的标题
     * @return 匹配的日记列表
     */
    List<Diary> findByTitle(String title);

    /**
     * 根据 ID 查找日记
     * @param id 日记的 ID
     * @return 匹配的日记，如果未找到则返回 Optional.empty()
     */
    Optional<Diary> findById(Long id);

    /**
     * 根据用户 ID 获取该用户的所有日记，并按日期排序
     * @param userId 用户的 ID
     * @return 按日期排序的日记列表
     */
    List<Diary> findByUserIdOrderByCratetimeAsc(Long userId);

    // /**
    //  * 根据日期查找日记
    //  * @param date 日期
    //  * @return 匹配的日记列表
    //  */
    // List<Diary> findByCratetimeContaining(String date);

    /**
     * 根据关键词查找日记
     * @param keyword 关键词
     * @return 匹配的日记列表
     */
    List<Diary> findByContentContainingOrTitleContaining(String keyword, String keyword2);

    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId AND DATE(d.cratetime) = :date")
    Optional<Diary> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * 根据创建时间范围查找日记
     * @param start 起始时间
     * @param end 结束时间
     * @return 匹配的日记列表
     */
    List<Diary> findByCratetimeBetween(LocalDateTime start, LocalDateTime end);
}