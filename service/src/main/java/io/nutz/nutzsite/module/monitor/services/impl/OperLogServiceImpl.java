package io.nutz.nutzsite.module.monitor.services.impl;

import io.nutz.nutzsite.common.page.TableDataInfo;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.slog.bean.SlogBean;
import org.nutz.plugins.slog.service.SlogService;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志记录 服务层实现
 * 
 * @author haiming
 * @date 2019-04-18
 */
@IocBean(name="slogService", fields={"dao"})
public class OperLogServiceImpl extends SlogService {

	public List<SlogBean> query() {
		return dao().query(SlogBean.class,null);
	}

	public SlogBean fetch(String name) {
		return this.dao().fetch(SlogBean.class, name);
	}

	public SlogBean insert(SlogBean t) {
		return this.dao().insert(t);
	}
	public void delete(String[] ids) {
		this.dao().clear(SlogBean.class, Cnd.where("uu32", "in", ids));
	}


	/**
	 * 分页查询数据封装
	 * @param pageNumber
	 * @param pageSize
	 * @param cnd
	 * @return
	 */
	public TableDataInfo tableList(int pageNumber, int pageSize, Cnd cnd, String orderByColumn, String isAsc){
		Pager pager = this.dao().createPager(pageNumber, pageSize);
		if (Strings.isNotBlank(orderByColumn) && Strings.isNotBlank(isAsc)) {
			String orderby = dao().getEntity(SlogBean.class).getColumn(orderByColumn).getName();
			cnd.orderBy(orderby,isAsc);
		}
		List<SlogBean> list = this.dao().query(SlogBean.class, cnd, pager);
		return new TableDataInfo(list, this.dao().count(SlogBean.class,cnd));
	}

	public void cleanInfor(){
		List<SlogBean> list =this.query();
		List<String> ids =new ArrayList<>();
		list.forEach(data -> {
			ids.add(data.getUu32());
		});
		String[] array = new String[ids.size()];
		this.delete(ids.toArray(array));
	}
}
