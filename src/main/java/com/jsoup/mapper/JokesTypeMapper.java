package com.jsoup.mapper;

import com.jsoup.pojo.JokesType;
import com.jsoup.pojo.JokesTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JokesTypeMapper {
    int countByExample(JokesTypeExample example);

    int deleteByExample(JokesTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(JokesType record);

    int insertSelective(JokesType record);

    List<JokesType> selectByExample(JokesTypeExample example);

    JokesType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") JokesType record, @Param("example") JokesTypeExample example);

    int updateByExample(@Param("record") JokesType record, @Param("example") JokesTypeExample example);

    int updateByPrimaryKeySelective(JokesType record);

    int updateByPrimaryKey(JokesType record);
}