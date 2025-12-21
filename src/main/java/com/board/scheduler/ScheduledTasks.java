package com.board.scheduler;

import com.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 스케줄링된 작업을 관리하는 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final BoardService boardService;

    /**
     * 30일 이상 지난 임시저장 게시글을 매일 자정에 자동 삭제
     *
     * 크론 표현식: "0 0 0 * * ?" = 매일 자정(00:00:00)에 실행
     * - 초(0-59): 0
     * - 분(0-59): 0
     * - 시(0-23): 0
     * - 일(1-31): * (매일)
     * - 월(1-12): * (매월)
     * - 요일(0-7): ? (무관)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldDrafts() {
        log.info("Starting scheduled task: deleteOldDrafts");

        try {
            boardService.deleteOldDrafts();
            log.info("Successfully deleted old drafts (30+ days)");
        } catch (Exception e) {
            log.error("Error during deleteOldDrafts scheduled task", e);
        }
    }
}
