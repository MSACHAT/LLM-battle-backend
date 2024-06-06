package com.example.llm_rating.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduledTasks {

    private final GetLeaderboard getLeaderboard;

    @Scheduled(cron = "0 0 23 * * ?")
    public void performTaskAt23() {
        getLeaderboard.getExample();
    }
}
