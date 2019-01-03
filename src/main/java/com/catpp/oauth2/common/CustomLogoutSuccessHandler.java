package com.catpp.oauth2.common;

import com.catpp.oauth2.Oauth2Application;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * com.catpp.oauth2.common
 *
 * @Author cat_pp
 * @Date 2018/12/28
 * @Description 登出控制清空accesstoken
 */
@Component
public class CustomLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
        implements LogoutSuccessHandler {

    private static final String BEARER_AUTHENTICATION = "Bearer";
    private static final String HEADER_AUTHENTICATION = "authorization";

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        String token = request.getHeader(HEADER_AUTHENTICATION);
        if (StringUtils.isNotEmpty(token) && StringUtils.startsWith(token, BEARER_AUTHENTICATION)) {
//            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token.split(" ")[0]);
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token.split(" ")[1]);
            if (null != oAuth2AccessToken) {
                tokenStore.removeAccessToken(oAuth2AccessToken);
            }
        }

        // 查询令牌列表
        /*List<String> tokenValues = new ArrayList<>();
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(token.split(" ")[1]);
        if (null != tokens) {
            for (OAuth2AccessToken everyToken : tokens) {
                tokenValues.add(everyToken.getValue());
            }
        }*/
        // 登出
        /*Collection<OAuth2AccessToken> tokens1 = tokenStore.findTokensByClientIdAndUserName("clientId", "username");

        boolean isLogout = consumerTokenServices.revokeToken("tokenId");

        if (isLogout) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }*/
     }
}
