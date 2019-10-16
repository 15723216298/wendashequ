package com.chowzzx.wendashequ.mapper;

import com.chowzzx.wendashequ.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Chowzzx
 * @date 2019/10/16 - 7:39 AM
 */
@Mapper
public interface UserMapper {

    @Insert("insert into user (name, account_id,token,gmt_create,gmt_modified) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    void insert(User user);

    @Select("select * from user where token = #{token}")
    User findByToken(@Param("token") String token);
}
