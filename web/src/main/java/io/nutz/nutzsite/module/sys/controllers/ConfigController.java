package io.nutz.nutzsite.module.sys.controllers;

import com.alibaba.dubbo.config.annotation.Reference;
import io.nutz.nutzsite.common.base.Result;
import io.nutz.nutzsite.common.utils.DateUtils;
import io.nutz.nutzsite.common.utils.ShiroUtils;
import io.nutz.nutzsite.common.utils.excel.ExportExcel;
import io.nutz.nutzsite.module.sys.models.Config;
import io.nutz.nutzsite.module.sys.services.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.slog.annotation.Slog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

;

/**
 * 系统参数 信息操作处理
 * 
 * @author haiming
 * @date 2019-04-17
 */
@IocBean
@At("/template/sys/config")
public class ConfigController {
	private static final Log log = Logs.get();

	@Inject
	@Reference
	private ConfigService configService;
	
	@RequiresPermissions("sys:config:view")
	@At("")
	@Ok("th:/sys/config/config.html")
	public void index(HttpServletRequest req) {

	}

	/**
	 * 查询系统参数列表
	 */
	@RequiresPermissions("sys:config:list")
	@At
	@Ok("json")
	public Object list(@Param("pageNum")int pageNum,
					   @Param("pageSize")int pageSize,
					   @Param("name") String name,
					   HttpServletRequest req) {
		Cnd cnd = Cnd.NEW();
		if (!Strings.isBlank(name)){
			//cnd.and("name", "like", "%" + name +"%");
		}
		return configService.tableList(pageNum,pageSize,cnd);
	}

	/**
	 * 新增系统参数
	 */
	@At("/add")
	@Ok("th:/sys/config/add.html")
	public void add( HttpServletRequest req) {

	}

	/**
	 * 新增保存系统参数
	 */
	@RequiresPermissions("sys:config:add")
	@At
	@POST
	@Ok("json")
	@Slog(tag="系统参数", after="新增保存系统参数id=${args[0].configKey}")
	public Object addDo(@Param("..") Config config, HttpServletRequest req) {
		try {
			configService.insert(config);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	/**
	 * 修改系统参数
	 */
	@At("/edit/?")
	@Ok("th://sys/config/edit.html")
	public void edit(String id, HttpServletRequest req) {
		Config config = configService.fetch(id);
		req.setAttribute("config",config);
	}

	/**
	 * 修改保存系统参数
	 */
	@RequiresPermissions("sys:config:edit")
	@At
	@POST
	@Ok("json")
	@Slog(tag="系统参数", after="修改保存系统参数")
	public Object editDo(@Param("..") Config config, HttpServletRequest req) {
		try {
			if(Lang.isNotEmpty(config)){
				config.setUpdateBy(ShiroUtils.getSysUserId());
				config.setUpdateTime(new Date());
				configService.update(config);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	/**
	 * 删除系统参数
	 */
	@At("/remove")
	@Ok("json")
	@RequiresPermissions("sys:config:remove")
	@Slog(tag ="系统参数", after= "删除系统参数:${array2str(args[0])}")
	public Object remove(@Param("ids")String[] ids, HttpServletRequest req) {
		try {
			configService.delete(ids);
			return Result.success("system.success",ids);
		} catch (Exception e) {
			return Result.error("system.error");
		}
	}

	@At
	@Ok("raw")
	public Object export(HttpServletRequest req, HttpServletResponse resp){
		String fileName = "系统参数"+ DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
		List<Config> list =configService.query();
		try {
			new ExportExcel("系统参数", Config.class).setDataList(list).write(resp, fileName).dispose();
			return Result.success("system.success");
		} catch (IOException e) {
//			e.printStackTrace();
		}
		return Result.success("system.success");
	}

}
