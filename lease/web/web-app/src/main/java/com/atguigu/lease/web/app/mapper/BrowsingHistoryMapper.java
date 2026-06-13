package com.atguigu.lease.web.app.mapper;

import com.atguigu.lease.model.entity.BrowsingHistory;
import com.atguigu.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
* @author liubo
* @description 针对表【browsing_history(浏览历史)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.atguigu.lease.model.entity.BrowsingHistory
*/
public interface BrowsingHistoryMapper extends BaseMapper<BrowsingHistory> {

    /**
     * 分页查询浏览历史。自定义结果集中要使用collection则只能使用嵌套查询方式，单独编写查询语句
     * @param page
     * @param userId
     * @return
     */
    IPage<HistoryItemVo> getHistory(IPage<HistoryItemVo> page, Long userId);
}




