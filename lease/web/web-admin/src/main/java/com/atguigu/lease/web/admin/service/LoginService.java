package com.atguigu.lease.web.admin.service;

import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    /**
     *  获取图形验证码
     * @return
     */
    CaptchaVo getCaptcha();


    /**
     *  登录
     * @param loginVo
     * @return  token
     */
    String login(LoginVo loginVo);

    /**
     *  获取登录用户信息
     * @return
     */
    SystemUserInfoVo getUserInfo();
}
