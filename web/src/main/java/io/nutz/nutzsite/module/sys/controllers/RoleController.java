package io.nutz.nutzsite.module.sys.controllers;

import com.alibaba.dubbo.config.annotation.Reference;
import io.nutz.nutzsite.common.base.Result;
import io.nutz.nutzsite.common.utils.ShiroUtils;
import io.nutz.nutzsite.module.sys.models.Menu;
import io.nutz.nutzsite.module.sys.models.Role;
import io.nutz.nutzsite.module.sys.services.MenuService;
import io.nutz.nutzsite.module.sys.services.RoleService;
import io.nutz.nutzsite.module.sys.services.UserService;
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
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 角色管理
 * @author haiming
 */
@IocBean
@At("/template/sys/role")
public class RoleController {
    private static final Log log = Logs.get();

    @Inject
    @Reference
    private RoleService roleService;

    @Inject
    @Reference
    private UserService userService;

    @Inject
    @Reference
    private MenuService menuService;

    @At("")
    @Ok("th:/sys/role/role.html")
    @RequiresPermissions("sys:role:view")
    public void index(HttpServletRequest req) {

    }

    @At
    @Ok("json")
    public Object list(@Param("pageNum")int pageNum,
                       @Param("pageSize")int pageSize,
                       @Param("roleName") String roleName,
                       @Param("roleKey") String roleKey,
                       @Param("orderByColumn") String orderByColumn,
                       @Param("isAsc") String isAsc,
                       HttpServletRequest req) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(roleName)){
            cnd.and("role_name", "like", "%" + roleName +"%");
        }
        if (!Strings.isBlank(roleKey)){
            cnd.and("role_key", "=", roleKey);
        }
        return roleService.tableList(pageNum,pageSize,cnd,orderByColumn,isAsc,null);
    }

    @At("/add")
    @Ok("th:/sys/role/add.html")
    public void add(@Param("id") String id, HttpServletRequest req) {

    }

    @At("/edit/?")
    @Ok("th:/sys/role/edit.html")
    public void edit(String id, HttpServletRequest req) {
        Role data = roleService.fetch(id);
        req.setAttribute("role",data);
    }

    @At
    @POST
    @Ok("json")
    @RequiresPermissions("sys:role:add")
    @Slog(tag="角色", after="新增保存角色id=${args[0].id}")
    public Object addDo(@Param("..") Role data, HttpServletRequest req) {
        try {
            roleService.insert(data);
            //更新缓存
            List<Menu> menuList = menuService.getMenuList(ShiroUtils.getSysUserId());
            // 角色列表
            Set<String> roles =userService.getRoleCodeList(ShiroUtils.getSysUserId());
            // 功能列表
            Set<String> menus = userService.getPermsByUserId(ShiroUtils.getSysUserId());
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At
    @POST
    @Ok("json")
    @RequiresPermissions("sys:role:edit")
    @Slog(tag="角色", after="修改保存角色")
    public Object editDo(@Param("..") Role data, HttpServletRequest req) {
        try {
            if(Lang.isNotEmpty(data)){
                data.setUpdateBy(ShiroUtils.getSysUserId());
                data.setUpdateTime(new Date());
                roleService.update(data);
                //更新缓存
                List<Menu> menuList = menuService.getMenuList(ShiroUtils.getSysUserId());
                // 角色列表
                Set<String> roles =userService.getRoleCodeList(ShiroUtils.getSysUserId());
                // 功能列表
                Set<String> menus = userService.getPermsByUserId(ShiroUtils.getSysUserId());
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/remove")
    @Ok("json")
    @RequiresPermissions("sys:role:remove")
    @Slog(tag ="角色", after= "删除角色:${array2str(args[0])}")
    public Object remove(@Param("ids")String[] ids, HttpServletRequest req) {
        try {
            roleService.delete(ids);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At
    @POST
    @Ok("json")
    public Object checkRoleNameUnique(@Param("id") String id,
                                      @Param("roleName") String roleName,
                                      @Param("roleKey") String roleKey, HttpServletRequest req) {
        return roleService.checkRoleNameUnique(id,roleName,roleKey);
    }


}
