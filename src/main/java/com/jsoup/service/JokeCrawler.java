package com.jsoup.service;


import com.alibaba.fastjson.JSON;
import com.jsoup.mapper.JokesTypeMapper;
import com.jsoup.pojo.Jokes;
import com.jsoup.pojo.JokesType;
import com.jsoup.utils.IdWorker;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;


@Component("jokeCrawler")
public class JokeCrawler {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private JokesTypeMapper jokesTypeMapper;

    /**
     * 该页面没有内容或者内容为图片
     */
    public static int noContext = 0;

    /**
     * 从数据库中分类数据加载到Map中
     */
    private Map<String, Long> jokeTypeMap = new HashMap<>();

    /**
     * 爬取主体
     * @param addressId
     * @return
     * @throws Exception
     */
    public Jokes getJokeByAddressId(int addressId) throws Exception {
        Connection connection_context = Jsoup
                .connect("http://xiaohua.zol.com.cn/detail60/" + addressId + ".html")
                .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                .timeout(3000);
        Connection connection_likeAndHate = Jsoup
                .connect("http://xiaohua.zol.com.cn/index.php?c=Ajax_Xiaohua&a=XhVoteGoodBad&xhId=" + addressId)
                .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                .timeout(3000);
        if (connection_context.execute().statusCode() == 200) {
            Document document_context = connection_context.get();
            //笑话正文
            String text = getText(document_context);
            //若没有正文则返回
            if (text == null || text.trim().length() == 0) {
                System.out.println("地址" + addressId + "：没有内容或者内容为图片");
                noContext++;
                return null;
            }
            //若有正文则新建对象，输入正文
            Jokes joke = new Jokes();

            joke.setText(text);
            //id
            joke.setId(idWorker.nextId());
            //笑话题目
            String title = getTitle(document_context);
            joke.setTitle(title);
            //笑话来源
            String source = getSource(document_context);
            joke.setSource(source);
            //赞和踩
            if (connection_likeAndHate.execute().statusCode() == 200) {
                Document document_likeAndHate = connection_likeAndHate.get();
                List<String> likeAndHate = getLikeAndHate(document_likeAndHate, addressId);
                String likes = likeAndHate.get(0);
                String hates = likeAndHate.get(1);
                joke.setLikes(Integer.parseInt(likes));
                joke.setHates(Integer.parseInt(hates));
            }
            //创造时间
            joke.setCreatetime(new Date());
            //修改时间
            joke.setUpdatetime(new Date());
            //笑话分类id
            if (addressId >= 2209) {
                String typeName = getTypeName(document_context);
                Long typeId = jokeTypeMap.get(typeName);
                if (typeId != null) {
                    joke.setTypeId(typeId);
                }
            }

            System.out.println("地址" + addressId + ": OK 线程：" + Thread.currentThread().getName());
            return joke;
        } else {
            System.out.println("地址" + addressId + ": 不可用");
            return null;
        }

    }

    /**
     * 将redis中的缓存加载到缓存
     */
    public void importJokeTypeMap() {
        List<JokesType> jokesTypeList = jokesTypeMapper.selectByExample(null);
        for (JokesType jokesType : jokesTypeList) {
            jokeTypeMap.put(jokesType.getName(), jokesType.getId());
        }
    }

    /**
     * 获取笑话题目
     * @param document
     * @return
     */
    private String getTitle(Document document) {
        Elements title = document
                .select(".wrapper")
                .select(".clearfix")
                .select(".main")
                .select(".section")
                .select(".article")
                .select(".article-header")
                .select("h1");
        return title.first().text();
    }

    /**
     * 获取笑话来源
     * @param document
     * @return
     */
    private String getSource(Document document) {
        Elements source = document
                .select(".wrapper")
                .select(".clearfix")
                .select(".main")
                .select(".section")
                .select(".article")
                .select(".article-header")
                .select(".article-source");
        String[] split = source.text().split("：");
        if (split.length >= 2) {
            return source.text().split("：")[1];
        } else {
            return "";
        }
    }

    /**
     * 获取笑话正文
     * @param document
     * @return
     */
    private String getText(Document document) {
        Elements text = document
                .select(".wrapper")
                .select(".clearfix")
                .select(".main")
                .select(".section")
                .select(".article")
                .select(".article-text");
        return text.text();
    }

    /**
     * 获取赞和踩
     * @param document_likeAndHate
     * @param addressId
     * @return
     * @throws IOException
     */
    private List<String> getLikeAndHate(Document document_likeAndHate, int addressId) throws IOException {
        Elements body = document_likeAndHate.select("body");
        try {
            Map map = JSON.parseObject(body.text(), Map.class);
            List<String> likeAndHate = (List<String>) map.get(addressId + "");
            return likeAndHate;
        } catch (Exception e) {
            List<String> list = new ArrayList<String>();
            list.add("0");
            list.add("0");
            return list;
        }
    }

    /**
     * 获取属性名
     * @param document
     * @return
     */
    private String getTypeName(Document document) {
        Elements elements = document
                .select(".wrapper")
                .select(".location")
                .select(".clearfix")
                .select("a");
        return elements.get(3).text();
    }
}
