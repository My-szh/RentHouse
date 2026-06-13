package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Autowired
    private BrowsingHistoryService browsingHistoryService;

    @Autowired
    private RedisTemplate<String, Object> stringObjectRedisTemplate;


    @Override
    public IPage<RoomItemVo> pageItem(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        IPage<RoomItemVo> result = roomInfoMapper.pageItem(page, queryVo);
        return result;
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        //加入redis缓存，先更新数据库，再更新缓存。
        String key=RedisConstant.APP_ROOM_PREFIX+id;
        RoomDetailVo roomDetailVo = (RoomDetailVo) stringObjectRedisTemplate.opsForValue().get(key);
        // 缓存中没有数据,则从数据库中查询
        if(roomDetailVo==null){
            RoomInfo roomInfo = roomInfoMapper.selectById(id);
            if(roomInfo == null)
                return null;
            roomDetailVo = new RoomDetailVo();
            // 获取公寓信息
            ApartmentItemVo apartmentItemVo = apartmentInfoService.selectApartmentItemVoById(roomInfo.getApartmentId());
            // 获取房间图片信息
            List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, id);
            // 获取房间属性信息
            List<AttrValueVo> attrValueVoList = attrValueMapper.selectListByRoomId(id);
            // 获取房间配套信息
            List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);
            // 获取房间标签信息
            List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);
            // 获取房间支付方式信息
            List<PaymentType> paymentTypeList = paymentTypeMapper.selectListByRoomId(id);
            // 获取房间杂费信息,杂费信息由公寓统一管理
            List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(roomInfo.getApartmentId());
            // 获取房间租期信息
            List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);

            BeanUtils.copyProperties(roomInfo, roomDetailVo);
            roomDetailVo.setApartmentItemVo(apartmentItemVo);
            roomDetailVo.setGraphVoList(graphVoList);
            roomDetailVo.setAttrValueVoList(attrValueVoList);
            roomDetailVo.setFacilityInfoList(facilityInfoList);
            roomDetailVo.setLabelInfoList(labelInfoList);
            roomDetailVo.setPaymentTypeList(paymentTypeList);
            roomDetailVo.setFeeValueVoList(feeValueVoList);
            roomDetailVo.setLeaseTermList(leaseTermList);
            stringObjectRedisTemplate.opsForValue().set(key,roomDetailVo);
        }
        // 保存浏览记录
        browsingHistoryService.saveHistory(LoginUserHolder.getLoginUser().getUserId(),id);
        return roomDetailVo;
    }

    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(IPage<RoomItemVo> page, Long id) {
        return roomInfoMapper.pageItemByApartmentId(page, id);
    }
}




