package io.nutz.nutzsite.module.monitor.services;

import io.nutz.nutzsite.common.page.TableDataInfo;
import org.nutz.dao.Cnd;
import org.nutz.plugins.slog.bean.SlogBean;

import java.util.List;

/**
 * @Author: Haimming
 * @Date: 2019-10-21 11:35
 * @Version 1.0
 */
public interface OperLogService {
    public List<SlogBean> query();

    public SlogBean fetch(String name);

    public SlogBean insert(SlogBean t);

    public void delete(String[] ids);

    /**
     * 分页查询数据封装
     * @param pageNumber
     * @param pageSize
     * @param cnd
     * @return
     */
    public TableDataInfo tableList(int pageNumber, int pageSize, Cnd cnd, String orderByColumn, String isAsc);

    public void cleanInfor();

}
