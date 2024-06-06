package com.example.llm_rating.tasks;

import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;
import com.example.llm_rating.config.OssConfig;
import com.example.llm_rating.service.impl.FileServiceImpl;
import com.example.llm_rating.service.impl.GetLeaderboard;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@AllArgsConstructor
public class ScheduledTasks {

    private final GetLeaderboard getLeaderboard;
    private final FileServiceImpl fileService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void generateLeaderBoard() throws IOException, ClientException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedYesterday = yesterday.format(formatter);
        CompletableFuture<PutObjectResult> future = fileService.uploadFile("llmbattle",formattedYesterday+"-conv.json","Logs/");
        future.thenAccept(putObjectResult -> {
            try {
                getLeaderboard.computeElo();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }
}
