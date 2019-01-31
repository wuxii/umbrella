package com.harmony.umbrella.security;

import com.harmony.umbrella.security.userdetails.JwtUserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii
 */
public interface JwtTokenGenerator {

    /**
     * 根据用户信息生成jwt token
     *
     * @param userDetails 用户信息
     * @param request     http request
     * @return
     */
    String generate(JwtUserDetails userDetails, HttpServletRequest request);

}