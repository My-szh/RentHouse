package com.atguigu.lease.web.app.service;

import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;

public interface LoginService {

    /**
     * 获取验证码
     * @param phone
     */
    void getCode(String phone);

    /**
     * 登录,成功返回jwt
     * @param loginVo
     * @return jwt
     */
    String login(LoginVo loginVo);
}
