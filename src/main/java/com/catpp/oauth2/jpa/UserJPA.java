package com.catpp.oauth2.jpa;

import com.catpp.oauth2.base.BaseRepository;
import com.catpp.oauth2.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;

/**
 * com.catpp.springbootjpa.jpa
 *
 * @Author cat_pp
 * @Date 2018/12/24
 * @Description
 */
public interface UserJPA extends BaseRepository<UserEntity, String>, Serializable {

    /**
     * 根据用户名不区分大小写查询
     *
     * @param username
     * @return
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.username) = LOWER(:username)")
    UserEntity findByUsernameCaseInsensitive(@Param("username") String username);
}
