package com.catpp.oauth2.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * com.catpp.springbootjpa.entity
 *
 * @Author cat_pp
 * @Date 2018/12/28
 * @Description AuthorityEntity 授权角色实体类
 */
@Data
@Entity
@Table(name = "authority")
public class AuthorityEntity {

    @Id
    @Column(name = "name")
    @Size(max = 50)
    @NotNull
    private String name;
}
