# oauth2
OAuth2 project

------------------------------------------------------------------------------------------------------------------------
# SpringBoot项目中使用SpringSecurity整合OAuth2设计项目API安全接口服务：
- OAuth是一个关于授权的开放网络标准，在全世界得到广泛的应用，目前是2.0版本。OAuth在“客户端”与“服务提供商”之间，设置
了一个授权层“authorization layer”。“客户端”不能直接登陆“服务提供商”，只能登陆授权层，以此将用户与客户端分离。“客
户端”登陆需要OAuth提供的令牌，否则将提示认证失败，客服端无法访问服务

## 学习目标：基于SpringBoot项目提供一个继承OAuth2安全框架的REST API服务端，必须获取访问授权令牌后才可以访问资源。

## OAuth2授权方式：
- 1、授权码模式（authorization code）
- 2、简化模式（implicit）
- 3、密码模式（resource owner password credentials）
- 4、客户端模式（client credentials）

### 1、授权码模式
- 授权码相对其他三种来说是功能比较完整、流程最安全严谨的授权方式，通过客户端的后台服务器与服务提供商的认证服务器交互来完成。
![授权码模式](/src/main/resources/images/授权码模式.png)

### 2、简化模式
- 这种模式不通过服务器端来完成，直接由浏览器发起请求获取令牌，令牌是完全暴露在浏览器中的，这种模式极力不推荐
![简化模式](/src/main/resources/images/简化模式.png)

### 3、密码模式
- 密码模式也是比较常用到的一种，客户端向授权服务器提供用户名、密码然后得到授权令牌。这种模式有种弊端，客户端需要存储用户
密码，但是对于用户来说信任度不高的平台是不可能让他们输入密码的。
![密码模式](/src/main/resources/images/密码模式.png)

### 4、客户端模式
- 客户端模式是客户端以自己的名义去授权服务器申请授权令牌，并不是完全意义上的授权。
![客户端模式](/src/main/resources/images/客户端模式.png)

## 使用密码模式进行测试：
- 引入相关依赖：Web、JPA、MySQL、Security、SpringSecurityOAuth2、Druid等

### 配置数据库：
#### 安全用户信息表
- 登录名、密码、邮箱、状态等
#### 安全角色信息表
- 角色名称
#### 用户角色关联表
#### AccessToken信息表
- 使用的是SpringSecurityOAuth2提供的Jdbc方式进行操作Token，所以需要根据标准创建对应的表结构
- token_id token authentication_id user_name client_id authentication refresh_token
#### RefreshToken信息表
- 刷新Token时需要用到refresh_token信息表
- token_id token authentication

> 创建用户信息、角色信息的实体,因为OAuth2内部操作数据库使用的JdbcTemplate我们只需要传入一个DataSource对象就可以了，实体
并不需要配置。

### 创建用户实体类UserEntity、角色实体类Authority
用户实体以及角色实体是用来配置SpringSecurity时用到的实体，我们配置SpringSecurity时需要使用SpringDataJPA从数据库中读取数据

## 开启SpringSecurity配置：
- 添加配置类：SecurityConfiguration
在配置类中注入自定义的UserService以及用户密码验证规则，使用ignoring()方法排除了HelloController内的公开方法，这里可以使用
通配符的形式进行排除。

## 配置安全资源服务器：
- 添加配置类：OAuth2Configuration
    > 创建一个OAuth2总配置类OAuth2Configuration，类内添加一个子类用于配置资源服务器
    > 在OAuth2Configuration配置类中添加子类ResourceServerConfiguration继承自ResourceServerConfigurerAdapter完成资源服务
    器的配置，使用@EnableResourceServer注解来开启资源服务器，因为整合SpringSecurity的缘故，需要配置登出时清空对应的
    access_token控制以及自定义401错误内容（authenticationEntryPoint），在配置类中排除了对/hello/say公开地址拦截以及
    /secure下的所有地址都必须授权才可以访问。

### 自定义401错误码内容：
    > CustomAuthenticationEntryPoint：配置如果没有权限访问接口时我们返回的错误码以及错误内容
### 定义登出控制：
    > CustomLogoutSuccessHandler：当退出系统时需要访问SpringSecrutiy的logout方法来清空对应的session信息，那退出后该用户
    的access_token还依然存在那就危险了，一旦别人知道该token就可以使用之前登录用户的权限来操作业务

## 开启OAuth2验证服务器：
- 在OAuth2Configuration配置类中添加一个子类，用于开启OAuth2的验证服务器
> 创建一个名叫AuthorizationServerConfiguration的类继承自AuthorizationServerConfigurerAdapter并且实现
EnvironmentAware（读取properties文件需要）接口，并使用@EnableAuthorizationServer注解开启验证服务器，使用
SpringSecurityOAuth2内定义的JdbcStore来操作数据库中的Token，如果有需要也可以通过SpringDataJPA自定义Sotre

> **springboot2.x在开启OAuth2验证服务器时，secret(passwordEncoder.encode(propertyMap.get(PROP_SECRET)))需要加密，不然报
错：Encoded password does not look like BCrypt**

## 测试：
1.访问localhost:8082/hello/say
    返回
    ```
    hello User
    ```
2.访问localhost:8082/secure/say
      返回
      ```
      {
          "timestamp": "2019-01-02T08:07:24.475+0000",
          "status": 401,
          "error": "Unauthorized",
          "message": "Access Denied",
          "path": "/secure/say"
      }
      ```

### 获取AccessToken：
> **springboot2.x在开启OAuth2验证服务器时，secret(passwordEncoder.encode(propertyMap.get(PROP_SECRET)))需要加密，不然报
错：Encoded password does not look like BCrypt**
- 访问http://localhost:8082/oauth/token?username=admin&password=admin&grant_type=password
grant_type使用到了password模式，在上面的配置中就是配置的客户端（catpp_home_pc）可以执行的模式有两种：
password、refresh_token。获取access_token需要添加客户端的授权信息clientid、secret，通过Postman工具的头授权信息即可输出对
应的值就可以完成Basic Auth的加密串生成
- 使用postman的话，配置Authorization面板：
    type:Basic Auth
    username:catpp_home_pc
    password:catpp_secret

    返回：
    ```
    {
        "access_token": "83946b7a-1efe-4b71-881e-499dfffde791",
        "token_type": "bearer",
        "refresh_token": "9d571e60-82fd-42b8-9022-16d70d217e1b",
        "expires_in": 1799,
        "scope": "read write"
    }
    ```
- 使用AccessToken访问：http://localhost:8082/secure/say?access_token=83946b7a-1efe-4b71-881e-499dfffde791
    返回：
    ```
    Secure Hello User
    ```
- 当AccessToken过期之后：访问http://localhost:8082/secure/say?access_token=83946b7a-1efe-4b71-881e-499dfffde791
    返回：
    ```
    {
        "error": "invalid_token",
        "error_description": "Access token expired: 83946b7a-1efe-4b71-881e-499dfffde791"
    }
    ```
- 根据refresh_token刷新AccessToken：http://localhost:8082/oauth/token?grant_type=refresh_token&refresh_token=9d571e60-82fd-42b8-9022-16d70d217e1b
    返回：
    ```
    {
        "access_token": "fb5c5f26-0735-45f2-aa80-60beb95154d8",
        "token_type": "bearer",
        "refresh_token": "9d571e60-82fd-42b8-9022-16d70d217e1b",
        "expires_in": 1799,
        "scope": "read write"
    }
    ```

-参数说明：
> access_token：本地访问获取到的access_token，会自动写入到数据库中
token_type：获取到的access_token的授权方式
refersh_token：刷新token时所用到的授权
tokenexpires_in：有效期（从获取开始计时，值秒后过期）
scope：客户端的接口操作权限（read：读，write：写）

## 退出登陆：
- 访问：http://localhost:8082/oauth/logout
- 参数：Headers [{"key":"authorization","value":"Bearer c1772af7-b2d7-4edb-8e80-7dd8a5d7b91b"}]
- 参数说明：c1772af7-b2d7-4edb-8e80-7dd8a5d7b91b:tokenValue

## 总结：
主要讲了SpringBoot作为框架基础上配置SpringSecurity安全框架整合OAuth2安全框架做双重安全，讲解如何通过数据库的形式获取到授
权用户信息以及角色列表，通过内存配置的OAuth2的客户端配置来获取access_token以及如何使用access_token访问受保护的资源接口。

