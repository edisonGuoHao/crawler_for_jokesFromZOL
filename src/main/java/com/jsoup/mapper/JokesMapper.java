package com.jsoup.mapper;

import com.jsoup.pojo.Jokes;
import com.jsoup.pojo.JokesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JokesMapper {
    int countByExample(JokesExample example);

    int deleteByExample(JokesExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Jokes record);

    int insertSelective(Jokes record);

    List<Jokes> selectByExample(JokesExample example);

    Jokes selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Jokes record, @Param("example") JokesExample example);

    int updateByExample(@Param("record") Jokes record, @Param("example") JokesExample example);

    int updateByPrimaryKeySelective(Jokes record);

    int updateByPrimaryKey(Jokes record);

    void insertJokeList(@Param("list") List<Jokes> list);
}