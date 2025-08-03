package com.emocloud.model.emolog;

import com.emocloud.model.User;
import com.emocloud.repository.emolog.DiaryRepository;
import com.emocloud.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DiaryAutoCreationTask {

    @Autowired
    private UserService userService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    public void createDiariesForUsers() {
        List<User> users = userService.getAllUsers();
        String currentDate = LocalDate.now().toString();
        for (User user : users) {
            Diary diary = new Diary();
            diary.setTitle("Diary for " + currentDate);
            diary.setContent("");
            diary.setCratetime(LocalDateTime.now());
            diary.setUpdatetime(LocalDateTime.now());
            diary.setSharetosocial(false);
            diary.setUser(user);
            diaryRepository.save(diary);
        }
    }
}