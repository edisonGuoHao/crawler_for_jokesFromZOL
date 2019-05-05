package com.jsoup.service;

import com.jsoup.controller.JokeApplication;
import com.jsoup.mapper.JokesMapper;
import com.jsoup.pojo.Jokes;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.jsoup.controller.JokeApplication.redisSetName;


public class JokeEngine implements Runnable {

    //开始索引
    private int start;

    //结束索引
    private int end;

    //剩下的笑话
    private Integer leftJoke;

    //爬取数据主题
    private JokeCrawler jokeCrawler;

    /**
     * 传入数据库的list
     */
    private List<Jokes> jokesList = new ArrayList<>();

    /**
     * redis中的缓存Id,可以重复爬取
     */
    private Set<String> jokeSourceIdSet;

    private JokesMapper jokesMapper;

    private RedisTemplate redisTemplate;


    /**
     * 404,没有找到对应页面计数器
     */
    public static int errorCounter = 0;


    public JokeEngine(int start, int end) throws IOException {
        this.start = start;
        this.end = end;
        leftJoke = end - start + 1;

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/applicationContext-dao.xml");
        jokeCrawler = applicationContext.getBean("jokeCrawler", JokeCrawler.class);
        jokeCrawler.importJokeTypeMap();

        jokesMapper = applicationContext.getBean("jokesMapper", JokesMapper.class);
        redisTemplate = applicationContext.getBean("redisTemplate", RedisTemplate.class);

        jokeSourceIdSet = redisTemplate.boundSetOps(redisSetName).members();
    }

    @Override
    public void run() {
        for (int i = start; i <= end; i++) {
            //如果jokeSourceIdSet中包含该Id,则返回
            if (jokeSourceIdSet.contains(i)) {
                continue;
            }
            //如果不包含则在redis中加入id
            redisTemplate.boundSetOps(redisSetName).add(i);
            try {
                //获取id对应joke
                Jokes joke = jokeCrawler.getJokeByAddressId(i);
                //剩下joke--
                leftJoke--;
                if (joke != null) {
                    jokesList.add(joke);
                }
            } catch (Exception e) {
                System.out.println("线程 " + Thread.currentThread().getName() + "：404");
                leftJoke--;
                errorCounter++;
            } finally {
                //如果jokesList大小达到100，统一传入数据库
                if (jokesList.size() >= JokeApplication.updateSize || leftJoke.intValue() <= 0) {
                    //保存笑话
                    System.out.println("线程" + Thread.currentThread().getName() + "的jokeList已满，导入数据库");
                    jokesMapper.insertJokeList(jokesList);
                    jokesList.clear();
                }
            }
        }
    }


}
