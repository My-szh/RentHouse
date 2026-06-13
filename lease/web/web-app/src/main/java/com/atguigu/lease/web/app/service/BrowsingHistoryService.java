package com.atguigu.lease.web.app.service;

import com.atguigu.lease.model.entity.BrowsingHistory;
import com.atguigu.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liubo
* @description 针对表【browsing_history(浏览历史)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface BrowsingHistoryService extends IService<BrowsingHistory> {
    /**
     * 分页获取浏览历史
     * @param page
     * @return
     */
    IPage<HistoryItemVo> getHistory(IPage<HistoryItemVo> page, Long userId);

    /**
     * 保存浏览历史
     * @param userId 用户id
     * @param roomId   房间id
     */
    void saveHistory(Long userId, Long roomId);
}
