package com.atguigu.lease.web.admin.service;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【apartment_info(公寓信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface ApartmentInfoService extends IService<ApartmentInfo> {

    /**
     * 保存或更新公寓信息
     * 通过保存或更新中间表实现
     * 实现方式
     * 1.判断id是否为空，如果为空则保存，不为空则更新
     * 从apartmentSubmitVo中抽取公寓基本信息，
     *      基本信息直接调用父类IService的saveOrUpdate方法保存或更新
     * 2、公寓其他信息：
     *      如果是更新，先删除旧数据，再保存新数据
     *
     *      如果是保存，则直接保存
     * @param apartmentSubmitVo
     */
    void saveOrUpdateApartmentInfo(ApartmentSubmitVo apartmentSubmitVo);


    /**
     * 根据条件分页查询公寓列表
     * 返回ApartmentItemVo：即公寓基本信息，总房间数，空闲房间数。
     * 总房间数从room_info表中查询
     * 空闲房间数从leaseAgreement表中查询
     * 将查询语句拼接到SQL中。
     *
     * @param page
     * @param queryVo
     * @return
     */
    IPage<ApartmentItemVo> pageItem(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo);


    /**
     * 根据id获取公寓详细信息
     * @param id
     * @return
     */
    ApartmentDetailVo getDetailById(Long id);

    /**
     * 根据id删除公寓信息
     * @param id
     */
    void removeInfoById(Long id);
}
