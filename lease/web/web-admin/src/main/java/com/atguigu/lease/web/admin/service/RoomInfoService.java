package com.atguigu.lease.web.admin.service;

import com.atguigu.lease.model.entity.RoomInfo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【room_info(房间信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface RoomInfoService extends IService<RoomInfo> {

    /**
     * 保存或更新房间信息
     * 根据id判断是保存还是更新
     * 更新：先删除原有信息，再保存
     * 保存：直接保存
     * @param roomSubmitVo
     */
    void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo);


    /**
     * 根据id删除房间信息
     *
     * @return
     */
    void removeInfoById(Long id);


    /**
     * 根据条件分页查询房间列表
     * 返回结果：
     * leaseEndDate ：租约结束日期     lease_agreement表中获取
     * isCheckIn : 当前入住状态       lease_agreement表中room_id存在, 则入住中
     * apartmentInfo : 所属公寓信息  apartmentInfo表中获取
     * roomInfo : 房间信息
     * @param page
     * @param queryVo
     * @return
     */
    IPage<RoomItemVo> pageItem(IPage<RoomItemVo> page, RoomQueryVo queryVo);

    /**
     * 根据id获取房间详细信息
     * @param id
     * @return RoomDetailVo
     */
    RoomDetailVo getDetailById(Long id);
}
