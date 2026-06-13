package com.atguigu.lease.web.app.service;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.LeaseTerm;
import com.atguigu.lease.model.entity.PaymentType;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service
 * @createDate 2023-07-26 11:12:39
 */
public interface LeaseAgreementService extends IService<LeaseAgreement> {
    /**
     * 根据登录用户名获取个人租约基本信息列表
     *
     * @param username
     * @return
     */
    List<AgreementItemVo> listItem(String username);

    /**
     * 根据租约id获取租约详情
     *
     * @param id
     * @return
     */
    AgreementDetailVo getDetailById(Long id);

    /**
     * 根据房间id获取付款方式列表
     *
     * @param id
     * @return
     */
    List<PaymentType> listByRoomId(Long id);

    /**
     * 根据房间id获取租期列表
     *
     * @param id
     * @return
     */
    List<LeaseTerm> getLeaseTerms(Long id);
}
