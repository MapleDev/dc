package me.crycry.file.common;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessQueue {
    public static Queue<TaskRunnable> taskQueue = new ConcurrentLinkedQueue<TaskRunnable>();
    public static boolean processing = false;
    public static void run(){
        Thread thread = new Thread(taskQueue.poll());
        thread.start();
    }



}
