package com.jsoup.controller;

import com.jsoup.service.JokeEngine;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jsoup.service.JokeEngine.errorCounter;
import static com.jsoup.service.JokeCrawler.noContext;

public class JokeApplication {

    //采用20个线程，一共59402个页面
    public static final int threadNumber = 8;
    public static final int totalJoke = 59402;
    public static final String redisSetName = "jokeSourceIdSet5";
    public static final int updateSize = 100;

    public static void main(String[] args) {
        //执行爬取数据
        letsGo(threadNumber, totalJoke);
    }

    private static void letsGo(int threadNumber, int totalJoke) {
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

        //开始爬数据
        System.out.println("----开始----");
        Long startTime = System.currentTimeMillis();

        //将totalJoke平均分为threadNumber份
        int start;
        int end;
        //step是步长
        double step = totalJoke / threadNumber;
        for (int i = 1; i <= threadNumber; i++) {
            start = (int) Math.ceil(step * (i - 1));
            end = (int) Math.floor(step * i);
            try {
                executorService.submit(new JokeEngine(start, end));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //判断正在运行的线程是否为1，若是1，则停止
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                System.out.println("----结束----");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //结束数据输出
        long endTime = System.currentTimeMillis();
        System.out.println("一共用时：" + (endTime - startTime) / 1000 + "秒");
        System.out.println(noContext + "个页面没有内容或者内容为图片");
        System.out.println(errorCounter + "个没有对应页面");
    }
}
