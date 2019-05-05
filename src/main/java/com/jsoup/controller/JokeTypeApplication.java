package com.jsoup.controller;

import com.jsoup.mapper.JokesTypeMapper;
import com.jsoup.pojo.JokesType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component("jokeTypeApplication")
public class JokeTypeApplication {

    @Autowired
    private JokesTypeMapper jokesTypeMapper;


    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/applicationContext-dao.xml");
        JokeTypeApplication jokeTypeApplication = applicationContext.getBean("jokeTypeApplication", JokeTypeApplication.class);
        jokeTypeApplication.importJokeType();
        System.out.println("笑话类型导入完成");
    }

    public void importJokeType() throws IOException {
        Document document = Jsoup.connect("http://xiaohua.zol.com.cn/")
                .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)").get();
        Elements jokeTypes = document.select(".wrapper")
                .select(".clearfix")
                .select(".main")
                .select(".section")
                .select(".classification-section")
                .select("ul")
                .select("li");
        for (Element jokeType : jokeTypes) {
            String jokeTypeName = jokeType.text().split("\\(")[0];
            System.out.println(jokeTypeName);
            JokesType jokesType = new JokesType();
            jokesType.setName(jokeTypeName);
            jokesTypeMapper.insert(jokesType);
        }
    }
}
