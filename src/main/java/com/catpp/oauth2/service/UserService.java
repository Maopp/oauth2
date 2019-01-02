package com.catpp.oauth2.service;

import com.catpp.oauth2.entity.AuthorityEntity;
import com.catpp.oauth2.entity.UserEntity;
import com.catpp.oauth2.jpa.UserJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

/**
 * com.catpp.springbootjpa.security
 *
 * @Author cat_pp
 * @Date 2018/12/27
 * @Description 实现SpringSecurity内的UserDetailsService接口来完成自定义查询用户的逻辑
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserJPA userJPA;

    /**
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String lowerUsername = username.toLowerCase();
        UserEntity userEntity = userJPA.findByUsernameCaseInsensitive(lowerUsername);
        if (null == userEntity) {
            throw new UsernameNotFoundException("User" + lowerUsername + "was not found in the database");
        }
        // 获取用户所有的权限集合，添加到SpringSecurity需要的集合中
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (AuthorityEntity authorityEntity : userEntity.getAuthoritySet()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authorityEntity.getName());
            grantedAuthorities.add(grantedAuthority);
        }
        // 返回一个SpringSecurity需要的用户对象
        return new User(userEntity.getUsername(), userEntity.getPassword(), grantedAuthorities);
    }
}
