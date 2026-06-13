package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomAttrValueService roomAttrValueService;

    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private RoomLeaseTermService roomLeaseTermService;

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    /*@Autowired
    private ApartmentInfoService apartmentInfoService;
    调用service，可能会发生循环依赖。
    */

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private RoomAttrValueMapper roomAttrValueMapper;

    @Autowired
    private RoomFacilityMapper roomFacilityMapper;

    @Autowired
    private RoomLeaseTermMapper roomLeaseTermMapper;

    @Autowired
    private RoomPaymentTypeMapper roomPaymentTypeMapper;

    @Autowired
    private RoomLabelMapper roomLabelMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {
        // 保存或更新房间基本信息
        super.saveOrUpdate(roomSubmitVo);
        Long id = roomSubmitVo.getId();
        if(id != null){
            // 删除属性信息 room_attr_value
            LambdaQueryWrapper<RoomAttrValue> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RoomAttrValue::getRoomId, id);
            roomAttrValueService.remove(queryWrapper);

            //删除配套信息
            LambdaQueryWrapper<RoomFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(RoomFacility::getRoomId, id);
            roomFacilityService.remove(facilityQueryWrapper);

            //删除标签信息
            LambdaQueryWrapper<RoomLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(RoomLabel::getRoomId, id);
            roomLabelService.remove(labelQueryWrapper);

            //删除支付方式列表
            LambdaQueryWrapper<RoomPaymentType> paymentTypeQueryWrapper  = new LambdaQueryWrapper<>();
            paymentTypeQueryWrapper.eq(RoomPaymentType::getRoomId, id);
            roomPaymentTypeService.remove(paymentTypeQueryWrapper);

            //删除可选租期列表
            LambdaQueryWrapper<RoomLeaseTerm> leaseTermQueryWrapper = new LambdaQueryWrapper<>();
            leaseTermQueryWrapper.eq(RoomLeaseTerm::getRoomId, id);
            roomLeaseTermService.remove(leaseTermQueryWrapper);

            //删除图片列表
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemId, id);
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoService.remove(graphQueryWrapper);

            //删除房间信息的同时删除缓存。
            redisTemplate.delete(RedisConstant.APP_LOGIN_PREFIX + roomSubmitVo.getId());
        }

        // 保存属性信息 room_attr_value
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if(!CollectionUtils.isEmpty(attrValueIds)){
            //RoomAttrValue 使用@Bulider注解，使用builder()方法创建对象，不能使用new
            ArrayList<RoomAttrValue> roomAttrValues = new ArrayList<>();
            for (Long attrValueId : attrValueIds) {
                RoomAttrValue roomAttrValue = RoomAttrValue.builder()
                        .roomId(id)
                        .attrValueId(attrValueId)
                        .build();
                roomAttrValues.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValues);
        }
        // 保存配套信息
        List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(facilityInfoIds)){
            ArrayList<RoomFacility> roomFacilities = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                RoomFacility roomFacility = RoomFacility.builder()
                        .roomId(id)
                        .facilityId(facilityInfoId)
                        .build();
                roomFacilities.add(roomFacility);
            }
            roomFacilityService.saveBatch(roomFacilities);
        }

        // 保存标签信息
        List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
        if(!CollectionUtils.isEmpty(labelInfoIds)){
            ArrayList<RoomLabel> roomLabels = new ArrayList<>();
            for (Long labelInfoId : labelInfoIds) {
                RoomLabel roomLabel = RoomLabel.builder()
                        .roomId(id)
                        .labelId(labelInfoId)
                        .build();
                roomLabels.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabels);
        }

        // 保存支付方式列表
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if(!CollectionUtils.isEmpty(paymentTypeIds)){
            ArrayList<RoomPaymentType> roomPaymentTypes = new ArrayList<>();
            for (Long paymentTypeId : paymentTypeIds) {
                RoomPaymentType roomPaymentType = RoomPaymentType.builder()
                        .roomId(id)
                        .paymentTypeId(paymentTypeId)
                        .build();
                roomPaymentTypes.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(roomPaymentTypes);
        }

        // 保存可选租期列表
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if(!CollectionUtils.isEmpty(leaseTermIds)){
            ArrayList<RoomLeaseTerm> roomLeaseTerms = new ArrayList<>();
            for (Long leaseTermId : leaseTermIds) {
                RoomLeaseTerm roomLeaseTerm = RoomLeaseTerm.builder()
                        .roomId(id)
                        .leaseTermId(leaseTermId)
                        .build();
                roomLeaseTerms.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(roomLeaseTerms);
        }

        // 添加图片列表
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphInfos = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemId(id);
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfos.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfos);
        }
    }



    @Override
    public void removeInfoById(Long id) {
        super.removeById(id);
        // 删除属性信息 room_attr_value
        LambdaQueryWrapper<RoomAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomAttrValue::getRoomId, id);
        roomAttrValueService.remove(queryWrapper);

        //删除配套信息
        LambdaQueryWrapper<RoomFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(RoomFacility::getRoomId, id);
        roomFacilityService.remove(facilityQueryWrapper);

        //删除标签信息
        LambdaQueryWrapper<RoomLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
        labelQueryWrapper.eq(RoomLabel::getRoomId, id);
        roomLabelService.remove(labelQueryWrapper);

        //删除支付方式列表
        LambdaQueryWrapper<RoomPaymentType> paymentTypeQueryWrapper  = new LambdaQueryWrapper<>();
        paymentTypeQueryWrapper.eq(RoomPaymentType::getRoomId, id);
        roomPaymentTypeService.remove(paymentTypeQueryWrapper);

        //删除可选租期列表
        LambdaQueryWrapper<RoomLeaseTerm> leaseTermQueryWrapper = new LambdaQueryWrapper<>();
        leaseTermQueryWrapper.eq(RoomLeaseTerm::getRoomId, id);
        roomLeaseTermService.remove(leaseTermQueryWrapper);

        //删除图片列表
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
        graphInfoService.remove(graphQueryWrapper);

        //8.删除缓存
        redisTemplate.delete(RedisConstant.APP_ROOM_PREFIX + id);
    }



    @Override
    public IPage<RoomItemVo> pageItem(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        IPage<RoomItemVo> result = roomInfoMapper.pageItem(page, queryVo);
        return result;
    }

    /**
     * 使用redis缓存，并利用互斥锁解决缓存击穿问题
     * @param
     * @return
     */
    @Override
    public RoomDetailVo getDetailById(Long id) {
        String key=RedisConstant.APP_ROOM_PREFIX+id;
        RoomDetailVo roomDetailVo = (RoomDetailVo) redisTemplate.opsForValue().get(key);
        //缓存命中
        if(roomDetailVo!=null){
            return roomDetailVo;
        }

        //缓存未命中,重构缓存
        String lockKey="lock:admin"+id;
        roomDetailVo = new RoomDetailVo();
        try{
            //尝试获取锁
            boolean isLock = tryLock(lockKey);
            //获取锁失败,线程等待一段时间后重试
            if(!isLock){
                //Thread.sleep(50);
                return getDetailById(id);
            }
            //获取锁成功,查询数据库，重建缓存
            RoomInfo roomInfo = super.getById(id);
            Long apartmentId = roomInfo.getApartmentId();
            ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(apartmentId);

            //获取图片信息
            List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, id);
            //获取属性信息
            List<AttrValueVo> attrValueVoList=roomAttrValueMapper.selectRoomAttrValueList(id);
            //获取配套信息
            List<FacilityInfo> facilityInfoList=roomFacilityMapper.selectRoomFacilityList(ItemType.ROOM,id);
            //获取标签信息
            List<LabelInfo> labelInfoList=roomLabelMapper.selectRoomLabelList(ItemType.ROOM,id);
            //获取支付方式列表
            List<PaymentType> paymentTypeList=roomPaymentTypeMapper.selectRoomPaymentTypeList(id);
            //获取可选租期列表
            List<LeaseTerm> leaseTermList=roomLeaseTermMapper.selectRoomLeaseTermList(id);

            BeanUtils.copyProperties(roomInfo, roomDetailVo);
            roomDetailVo.setApartmentInfo(apartmentInfo);
            roomDetailVo.setGraphVoList(graphVoList);
            roomDetailVo.setAttrValueVoList(attrValueVoList);
            roomDetailVo.setFacilityInfoList(facilityInfoList);
            roomDetailVo.setLabelInfoList(labelInfoList);
            roomDetailVo.setPaymentTypeList(paymentTypeList);
            roomDetailVo.setLeaseTermList(leaseTermList);
            redisTemplate.opsForValue().set(key,roomDetailVo);

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            //释放锁
            unlock(lockKey);
        }
        return roomDetailVo;
    }

    //获取锁
    public boolean tryLock(String lockKey){
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10L, TimeUnit.SECONDS);
        return BooleanUtils.isTrue(isLock);
    }

    //释放锁
    public void unlock(String lockKey){
        redisTemplate.delete(lockKey);
    }
}




