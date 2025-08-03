package com.emocloud.service.emolog;

import com.emocloud.dto.EmotionLog.DiaryDTO;
import com.emocloud.model.User;
import com.emocloud.model.emolog.Diary;
import com.emocloud.repository.emolog.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.emocloud.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.time.LocalDate;

/**
 * 情绪日记服务实现类，实现了 DiaryService 接口中的方法，负责具体的业务逻辑处理
 */
@Service
public class DiaryServiceImpl implements DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Optional<Diary> getDiaryById(Long id) {
        return diaryRepository.findById(id);
    }

    @Override
    public Diary updateDiary(Diary diary) {
        diary.setUpdatetime(LocalDateTime.now());
        return diaryRepository.save(diary);
    }

    @Override
    public DiaryDTO convertToDTO(Diary diary) {
        DiaryDTO dto = new DiaryDTO();
        dto.setId(diary.getId());
        dto.setTitle(diary.getTitle());
        dto.setContent(diary.getContent());
        dto.setCratetime(diary.getCratetime());
        dto.setUpdatetime(diary.getUpdatetime());
        dto.setSharetosocial(diary.isSharetosocial());
        dto.setImages(diary.getImages());
        dto.setVideos(diary.getVideos());

        if (diary.getUser() != null) {
            dto.setUserId(diary.getUser().getId());
        }
        dto.setFont(diary.getFont());
        dto.setLayout(diary.getLayout());
        dto.setDecoration(diary.getDecoration());
        return dto;
    }
    @Override
    public Diary convertToEntity(DiaryDTO diaryDTO) {
        Diary diary = new Diary();
        diary.setId(diaryDTO.getId());
        diary.setTitle(diaryDTO.getTitle());
        diary.setContent(diaryDTO.getContent());
        diary.setSharetosocial(diaryDTO.isSharetosocial());
        diary.setImages(diaryDTO.getImages());
        diary.setVideos(diaryDTO.getVideos());
        diary.setFont(diaryDTO.getFont());
        diary.setLayout(diaryDTO.getLayout());
        diary.setDecoration(diaryDTO.getDecoration());
        diary.setCratetime(diaryDTO.getCratetime());
        diary.setUpdatetime(diaryDTO.getUpdatetime());

        return diary;
    }
    @Override
    public Diary createDiary(DiaryDTO diaryDTO) {
        System.out.println("### 开始创建日记 ###");
        System.out.println("用户ID: " + diaryDTO.getUserId());

        try {
            User user = userRepository.findById(diaryDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + diaryDTO.getUserId()));

            System.out.println("找到用户: " + user.getUsername());

            // 转换 DTO 为实体
            Diary diary = convertToEntity(diaryDTO);
            diary.setUser(user);

            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            diary.setCratetime(now);
            diary.setUpdatetime(now);

            // 保存到数据库
            return diaryRepository.save(diary);
        } catch (Exception e) {
            System.out.println("### 创建日记时发生异常 ###");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    public List<Diary> getDiariesByUserId(Long userId) {
        return diaryRepository.findByUserId(userId);
    }

    @Override
    public String generateAISummary(String content, String emotionTag) {
        // 模拟生成摘要逻辑，实际可对接AI服务
        return "AI生成的摘要：基于情绪标签 " + emotionTag + "，内容摘要：" + content.substring(0, Math.min(content.length(), 50)) + "...";
    }

    @Override
    public String recommendMusicByEmotion(String emotionTag) {
        // 模拟推荐音乐逻辑，实际可根据情绪标签查询音乐库
        String[] musicUrls = {
                "https://example.com/happy_music.mp3",
                "https://example.com/sad_music.mp3",
                "https://example.com/relax_music.mp3"
        };
        Random random = new Random();
        return musicUrls[random.nextInt(musicUrls.length)];
    }

    @Override
    public List<String> filterImagesFromAlbum(String emotionTag) {
        // 模拟从相册筛选图片逻辑，实际可对接相册服务接口
        List<String> imageUrls = List.of(
                "https://example.com/" + emotionTag + "_image1.jpg",
                "https://example.com/" + emotionTag + "_image2.jpg"
        );
        return imageUrls;
    }
    @Override
    public List<Diary> getDiariesByUserIdSorted(Long userId) {
        // 调用Repository中按日期排序的方法
        return diaryRepository.findByUserIdOrderByCratetimeAsc(userId);
    }

    @Override
    public DiaryDTO editDiary(Long id, DiaryDTO diaryDTO) {
        Optional<Diary> existingDiary = diaryRepository.findById(id);
        if (existingDiary.isPresent()) {
            Diary diary = existingDiary.get();
            diary.setTitle(diaryDTO.getTitle());
            diary.setContent(diaryDTO.getContent());
            diary.setUpdatetime(LocalDateTime.now());
            diary.setFont(diaryDTO.getFont());
            diary.setLayout(diaryDTO.getLayout());
            diary.setDecoration(diaryDTO.getDecoration());
            diary.setImages(diaryDTO.getImages());
            diary.setVideos(diaryDTO.getVideos());

            Diary updatedDiary = diaryRepository.save(diary);
            return convertToDTO(updatedDiary);
        }
        return null;
    }
    @Override
    public boolean deleteDiary(Long id) {
        Optional<Diary> diary = diaryRepository.findById(id);
        if (diary.isPresent()) {
            diaryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    @Override
    public List<DiaryDTO> findDiariesByDate(LocalDateTime date) {
        // 获取当天的起始和结束时间
        LocalDateTime start = date.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        List<Diary> diaries = diaryRepository.findByCratetimeBetween(start, end);
        return diaries.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<DiaryDTO> findDiariesByKeyword(String keyword) {
        List<Diary> diaries = diaryRepository.findByContentContainingOrTitleContaining(keyword, keyword);
        return diaries.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<Diary> findTodaysDiary(Long userId) {
        LocalDate today = LocalDate.now();
        // 这里调用我们刚刚在 Repository 中定义的方法
        // 注意：这个方法的实现依赖于你是否将 cratetime 字段类型修改为日期时间类型
        return diaryRepository.findByUserIdAndDate(userId, today);
    }

    @Override
    public DiaryDTO appendDiaryContent(Long id, String appendContent) {
        Optional<Diary> existingDiary = diaryRepository.findById(id);
        if (existingDiary.isPresent()) {
            Diary diary = existingDiary.get();
            String oldContent = diary.getContent() == null ? "" : diary.getContent();
            // 如果原内容不为空，先加换行再追加
            String newContent = oldContent.isEmpty() ? appendContent : oldContent + "\n" + appendContent;
            diary.setContent(newContent);
            diary.setUpdatetime(LocalDateTime.now());
            Diary updatedDiary = diaryRepository.save(diary);
            return convertToDTO(updatedDiary);
        }
        return null;
    }
}