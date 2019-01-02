package com.catpp.oauth2.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * com.catpp.springbootjpa.entity
 *
 * @Author cat_pp
 * @Date 2018/12/28
 * @Description
 */
@Data
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {

    @Id
    @Column(name = "username", updatable = false, nullable = false)
    @Size(min = 0, max = 50)
    private String username;

    @Column(name = "password")
    @Size(min = 0, max = 255)
    private String password;

    @Column(name = "email")
    @Size(max = 50)
    private String email;

    @Column(name = "activationkey")
    @Size(max = 50)
    private String activationKey;

    @Column(name = "resetpasswordkey")
    @Size(max = 50)
    private String resetPasswordKey;

    @Column(name = "activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "authority")
    )
    private Set<AuthorityEntity> authoritySet;

}
