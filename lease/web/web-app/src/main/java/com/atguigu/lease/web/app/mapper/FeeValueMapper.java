package com.atguigu.lease.web.app.mapper;

import com.atguigu.lease.model.entity.FeeValue;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【fee_value(杂项费用值表)】的数据库操作Mapper
 * @createDate 2023-07-26 11:12:39
 * @Entity com.atguigu.lease.model.entity.FeeValue
 */
public interface FeeValueMapper extends BaseMapper<FeeValue> {

    /**
     * 根据公寓id查询费用值列表，房间杂费就是公寓杂费
     * @param id 公寓id
     * @return 费用值列表
     */
    List<FeeValueVo> selectListByApartmentId(Long id);

}




