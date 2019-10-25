package me.crycry.file.service;

import me.crycry.file.common.ProcessQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTaskService {


    @Scheduled(fixedDelay = 20000)
    public void process() {
        System.out.println("检测队列。。。。。");
        if (!ProcessQueue.processing&&!ProcessQueue.taskQueue.isEmpty()) {
            System.out.println("开始任务。。。。。。。");
            ProcessQueue.run();

        }
    }
}
