package io.nutz.nutzsite.module.sys.services.impl;

import com.alibaba.dubbo.config.annotation.Service;
import io.nutz.nutzsite.common.service.BaseServiceImpl;
import io.nutz.nutzsite.module.sys.models.Menu;
import io.nutz.nutzsite.module.sys.models.Role;
import io.nutz.nutzsite.module.sys.services.MenuService;
import io.nutz.nutzsite.module.sys.services.RoleService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.sql.Criteria;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author haiming
 */
@IocBean(args = {"refer:dao"})
@Service(interfaceClass = RoleService.class)
public class RoleServiceImpl  extends BaseServiceImpl<Role> implements RoleService {

    public RoleServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private MenuService menuService;


    /**
     * 新增角色
     *
     * @param data
     * @return
     */
    @Override
    public Role insert(Role data) {
        List<String> ids = new ArrayList<>();
        if (data != null && data.getMenuIds() != null) {
            if (Strings.isNotBlank(data.getMenuIds())) {
                ids = Arrays.asList(data.getMenuIds().split(","));
            }
        }
        if (ids != null && ids.size() > 0) {
            Criteria cri = Cnd.cri();
            cri.where().andInStrList("id", ids);
            List<Menu> menuList = menuService.query(cri);
            data.setMenus(menuList);
        }
        dao().insert(data);
        dao().insertRelation(data, "menus");
        return data;
    }

    /**
     * 更新角色
     *
     * @param data
     * @return
     */
    @Override
    public int update(Role data) {
        List<String> ids = new ArrayList<>();
        if (data != null && data.getMenuIds() != null) {
            if (Strings.isNotBlank(data.getMenuIds())) {
                ids = Arrays.asList(data.getMenuIds().split(","));
            }
            //清除已有关系
            Role tmpData = this.fetch(data.getId());
            this.fetchLinks(tmpData, "menus");
            dao().clearLinks(tmpData, "menus");
        }
        if (ids != null && ids.size() > 0) {
            Criteria cri = Cnd.cri();
            cri.where().andInStrList("id", ids);
            List<Menu> menuList = menuService.query(cri);
            data.setMenus(menuList);
        }
        int count = dao().update(data);
        dao().insertRelation(data, "menus");
        return count;
    }

    @Override
    public void delete(String[] ids) {
        List<Role> roleList =this.query(Cnd.where("id", "in", ids));
        roleList.forEach(role -> {
            dao().clearLinks(role, "menus");
        });

        this.dao().clear(Role.class, Cnd.where("id", "in", ids));
    }

    /**
     * 校验角色名称是否唯一
     * @param roleName
     * @return
     */
    @Override
    public boolean checkRoleNameUnique(String id, String roleName, String roleKey) {
        Cnd cnd = Cnd.NEW();
        if(Strings.isNotBlank(id)){
            cnd.and("id","!=",id);
        }
        if(Strings.isNotBlank(roleName)){
            cnd.and("role_name", "=", roleName);
        }
        if(Strings.isNotBlank(roleKey)){
            cnd.and("role_key", "=", roleKey);
        }
        List<Role> roleList = this.query(cnd);
        if (Lang.isEmpty(roleList)) {
            return true;
        }
        return false;
    }

}
