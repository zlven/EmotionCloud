package com.emocloud.service.emolog;

import com.emocloud.dto.EmotionLog.EmotionLogDTO;
import com.emocloud.model.User;
import com.emocloud.model.emolog.EmotionLog;
import com.emocloud.repository.UserRepository;
import com.emocloud.repository.emolog.EmotionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 情绪日志服务实现类：实现核心业务逻辑
 */
@Service
@Transactional // 事务管理（确保数据库操作的原子性）
public class EmotionLogServiceImpl implements EmotionLogService {

    private static final Logger log = LoggerFactory.getLogger(EmotionLogServiceImpl.class);

    @Autowired
    private EmotionLogRepository emotionLogRepository; // 日志数据访问层

    @Autowired
    private UserRepository userRepository; // 用户数据访问层

    @Override
    public Optional<EmotionLog> getEmotionLogById(Long id) {
        return emotionLogRepository.findById(id);
    }

    @Override
    public EmotionLog updateEmotionLog(EmotionLog emotionLog) {
        return emotionLogRepository.save(emotionLog); // save方法兼具新增和更新功能
    }

    @Override
    public EmotionLog createEmotionLog(EmotionLog emotionLog) {
        // 自动补全创建时间（如果前端未传递）
        if (emotionLog.getCratetime() == null || emotionLog.getCratetime().isEmpty()) {
            // 格式：yyyy-MM-dd HH:mm:ss（兼容前端日期格式化）
            emotionLog.setCratetime(LocalDate.now().format(DateTimeFormatter.ISO_DATE) + " " +
                    LocalDate.now().atStartOfDay().plusHours(12).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
        return emotionLogRepository.save(emotionLog);
    }

    @Override
    public void deleteEmotionLog(Long id) {
        emotionLogRepository.deleteById(id);
    }

    @Override
    public List<EmotionLog> getAllEmotionLogs() {
        return emotionLogRepository.findAll();
    }

    @Override
    public Page<EmotionLog> getAllEmotionLogs(Pageable pageable) {
        return emotionLogRepository.findAll(pageable);
    }

    @Override
    public List<EmotionLog> getEmotionLogsByUserId(Long userId) {
        return emotionLogRepository.findByUserId(userId);
    }

    /**
     * 核心方法：支持时间筛选和关键词搜索
     */
    @Override
    public Page<EmotionLog> getEmotionLogsByUserId(Long userId, Pageable pageable, String filterType, String keyword) {
        // 1. 计算时间范围（根据filterType）
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now(); // 结束时间固定为今天

        if ("week".equals(filterType)) {
            startDate = LocalDate.now().minusWeeks(1); // 本周：近7天
        } else if ("month".equals(filterType)) {
            startDate = LocalDate.now().minusMonths(1); // 本月：近30天
        }
        // filterType为all时，startDate为null（不限制开始时间）

        // 2. 转换日期为字符串（匹配数据库cratetime格式：yyyy-MM-dd）
        String start = startDate != null ? startDate.format(DateTimeFormatter.ISO_DATE) : null;
        String end = endDate.format(DateTimeFormatter.ISO_DATE);

        // 3. 根据关键词是否存在，调用不同的仓库方法
        if (keyword != null && !keyword.isEmpty()) {
            // 关键词搜索：匹配conclusion或tags（修复：移除重复的keyword参数）
            return emotionLogRepository.findByUserIdAndCratetimeBetweenAndConclusionContainingOrTagsContaining(
                    userId, start, end, keyword, pageable
            );
        } else {
            // 无关键词：仅按时间范围筛选
            return emotionLogRepository.findByUserIdAndCratetimeBetween(
                    userId, start, end, pageable
            );
        }
    }

    @Override
    public List<EmotionLog> getEmotionLogsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return emotionLogRepository.findByUserIdAndCratetimeBetween(
                userId,
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
        );
    }

    /**
     * 实体转DTO：确保前端获取的字段与后端一致
     */
    @Override
    public EmotionLogDTO convertToDTO(EmotionLog emotionLog) {
        if (emotionLog == null) return null;

        EmotionLogDTO dto = new EmotionLogDTO();
        dto.setId(emotionLog.getId());
        dto.setCratetime(emotionLog.getCratetime()); // 字段名：cratetime（与前端一致）
        dto.setEmotionvalue(emotionLog.getEmotionvalue()); // 字段名：emotionvalue（与前端一致）
        dto.setAigcchart(emotionLog.getAigcchart());
        dto.setConclusion(emotionLog.getConclusion());
        dto.setTags(emotionLog.getTags());
        // 传递用户ID而非User对象（避免前端处理复杂对象）
        dto.setUserId(emotionLog.getUser() != null ? emotionLog.getUser().getId() : null);

        return dto;
    }

    /**
     * DTO转实体：用于接收前端提交的数据
     */
    @Override
    public EmotionLog convertToEntity(EmotionLogDTO emotionLogDTO) {
        if (emotionLogDTO == null) return null;

        EmotionLog emotionLog = new EmotionLog();
        emotionLog.setId(emotionLogDTO.getId());
        emotionLog.setCratetime(emotionLogDTO.getCratetime());
        emotionLog.setEmotionvalue(emotionLogDTO.getEmotionvalue());
        emotionLog.setAigcchart(emotionLogDTO.getAigcchart());
        emotionLog.setConclusion(emotionLogDTO.getConclusion());
        emotionLog.setTags(emotionLogDTO.getTags());

        // 关联用户（通过userId查询User对象）
        if (emotionLogDTO.getUserId() != null) {
            Optional<User> user = userRepository.findById(emotionLogDTO.getUserId());
            user.ifPresent(emotionLog::setUser);
        }

        return emotionLog;
    }

    /**
     * 每周定时任务：自动生成用户上周的情绪总结日志
     * 每周一凌晨2点执行，分析上周一至周日的数据
     */
    @Scheduled(cron = "0 0 2 * * MON")
    public void generateWeeklyEmotionLogs() {
        log.info("开始执行每周情绪日志自动生成任务...");

        try {
            // 获取所有用户
            List<User> users = userRepository.findAll();

            // 获取上周的日期范围（周一到周日）
            LocalDate lastMonday = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
            LocalDate lastSunday = LocalDate.now().minusWeeks(1).with(DayOfWeek.SUNDAY);

            // 为每个用户生成上周的情绪日志
            for (User user : users) {
                generateWeeklyLogForUser(user.getId(), lastMonday, lastSunday);
            }

            log.info("每周情绪日志自动生成任务完成，共生成 {} 条日志", users.size());
        } catch (Exception e) {
            log.error("每周情绪日志自动生成任务失败", e);
        }
    }

    /**
     * 为单个用户生成指定日期范围的情绪总结日志
     */
    private void generateWeeklyLogForUser(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            // 1. 查询用户在指定日期范围内的所有情绪记录
            List<EmotionLog> dailyLogs = getEmotionLogsByDateRange(
                    userId,
                    startDate,
                    endDate
            );

            // 2. 如果没有记录，跳过生成
            if (dailyLogs.isEmpty()) {
                log.info("用户 {} 在 {} 至 {} 期间没有情绪记录，跳过生成周总结",
                        userId,
                        startDate.format(DateTimeFormatter.ISO_DATE),
                        endDate.format(DateTimeFormatter.ISO_DATE));
                return;
            }

            // 3. 计算平均情绪值
            double avgEmotionValue = dailyLogs.stream()
                    .mapToInt(EmotionLog::getEmotionvalue)
                    .average()
                    .orElse(50); // 默认值为50（中性情绪）

            // 4. 分析情绪趋势（上升/下降/平稳）
            String trend = analyzeEmotionTrend(dailyLogs);

            // 5. 生成AI总结文本
            String conclusion = generateAISummary(dailyLogs, trend);

            // 6. 生成标签
            List<String> tags = generateTags((int) avgEmotionValue);

            // 7. 创建新的周总结日志
            EmotionLog weeklySummary = new EmotionLog();
            weeklySummary.setUser(userRepository.findById(userId).orElse(null));
            weeklySummary.setEmotionvalue((int) avgEmotionValue);
            weeklySummary.setConclusion(conclusion);
            weeklySummary.setTags(tags);

            // 设置日志日期为上周日（表示这是上周的总结）
            String logDate = endDate.format(DateTimeFormatter.ISO_DATE) + " 12:00:00";
            weeklySummary.setCratetime(logDate);

            // 设置AI生成的图表URL（实际项目中可能需要调用AI服务生成）
            weeklySummary.setAigcchart("/api/emotion-chart/weekly/" + userId + "/" + endDate);

            // 8. 保存日志
            createEmotionLog(weeklySummary);

            log.info("成功为用户 {} 生成 {} 至 {} 的情绪总结日志",
                    userId,
                    startDate.format(DateTimeFormatter.ISO_DATE),
                    endDate.format(DateTimeFormatter.ISO_DATE));
        } catch (Exception e) {
            log.error("为用户 {} 生成情绪总结日志失败", userId, e);
        }
    }

    /**
     * 分析情绪趋势（上升/下降/平稳）
     */
    private String analyzeEmotionTrend(List<EmotionLog> logs) {
        if (logs.size() <= 1) return "平稳";

        // 按日期排序
        logs.sort(Comparator.comparing(EmotionLog::getCratetime));

        // 计算趋势（简单比较首尾情绪值）
        int firstValue = logs.get(0).getEmotionvalue();
        int lastValue = logs.get(logs.size() - 1).getEmotionvalue();

        if (lastValue > firstValue + 10) return "上升";
        if (lastValue < firstValue - 10) return "下降";
        return "平稳";
    }

    /**
     * 生成AI总结文本（实际项目中可以调用真正的AI服务）
     */
    private String generateAISummary(List<EmotionLog> logs, String trend) {
        int count = logs.size();
        double avgValue = logs.stream().mapToInt(EmotionLog::getEmotionvalue).average().orElse(50);

        String baseSummary = "本周你记录了" + count + "次情绪，平均情绪值为" + avgValue + "，整体情绪" + trend + "。";

        // 根据平均情绪值添加不同的建议
        if (avgValue >= 80) {
            return baseSummary + "你的情绪状态非常好，继续保持！";
        } else if (avgValue >= 60) {
            return baseSummary + "你的情绪状态稳定，继续保持健康的生活方式！";
        } else if (avgValue >= 40) {
            return baseSummary + "你最近情绪有些低落，建议适当放松，和朋友聊聊天。";
        } else {
            return baseSummary + "你最近情绪压力较大，建议寻求亲友或专业人士的支持。";
        }
    }

    /**
     * 根据情绪值生成标签
     */
    private List<String> generateTags(int emotionValue) {
        if (emotionValue >= 80) {
            return Arrays.asList("愉悦", "轻松", "满足");
        } else if (emotionValue >= 60) {
            return Arrays.asList("平静", "稳定", "日常");
        } else if (emotionValue >= 40) {
            return Arrays.asList("低落", "疲惫", "需要休息");
        } else {
            return Arrays.asList("焦虑", "压力大", "需要倾诉");
        }
    }
}