package io.nutz.nutzsite.module.sys.controllers;

import com.alibaba.dubbo.config.annotation.Reference;
import io.nutz.nutzsite.common.base.Result;
import io.nutz.nutzsite.common.bean.Amap;
import io.nutz.nutzsite.common.bean.Districts;
import io.nutz.nutzsite.common.utils.ShiroUtils;
import io.nutz.nutzsite.module.sys.models.Area;
import io.nutz.nutzsite.module.sys.services.AreaService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.slog.annotation.Slog;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

;

/**
 * 区域 信息操作处理
 *
 * @author haiming
 * @date 2019-04-11
 */
@IocBean
@At("/template/sys/area")
public class AreaController {
    private static final Log log = Logs.get();

    public static List<Area> areaList = new ArrayList<>();

    @Inject
    @Reference
    private AreaService areaService;

    @RequiresPermissions("sys:area:view")
    @At("")
    @Ok("th:/sys/area/area.html")
    public void index(HttpServletRequest req) {

    }

    /**
     * 查询区域列表
     */
    @RequiresPermissions("sys:area:list")
    @At
    @Ok("json")
    public Object list(@Param("name") String name, HttpServletRequest req) {
        Cnd cnd = Cnd.NEW();
//        if (!Strings.isBlank(name)) {
//            cnd.and("name", "like", "%" + name +"%");
//        }
        cnd.asc("adcode");
        return areaService.query(cnd);
    }

    /**
     * 新增区域
     */
    @At({"/add","/add/*"})
    @Ok("th:/sys/area/add.html")
    public void add(@Param("id") String id, HttpServletRequest req) {
        Area area = null;
        if (Strings.isNotBlank(id)) {
            area = areaService.fetch(id);
        }
        if (area ==null) {
            area =new Area();
            area.setParentId("0");
            area.setName("无");
        }
        req.setAttribute("area", area);
    }

    /**
     * 新增保存区域
     */
    @RequiresPermissions("sys:area:add")
    @At
    @POST
    @Ok("json")
    @Slog(tag="区域", after="新增保存区域id=${args[0].id}")
    public Object addDo(@Param("..") Area area, HttpServletRequest req) {
        try {
            areaService.insert(area);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    /**
     * 修改区域
     */
    @At("/edit/?")
    @Ok("th://sys/area/edit.html")
    public void edit(String id, HttpServletRequest req) {
        Area area = areaService.fetch(id);
        if (area != null) {
            Area parentData = areaService.fetch(area.getParentId());
            if (parentData != null) {
                area.setParentName(parentData.getName());
            }
        }
        req.setAttribute("area", area);
    }

    /**
     * 修改保存区域
     */
    @RequiresPermissions("sys:area:edit")
    @At
    @POST
    @Ok("json")
    @Slog(tag="区域", after="修改保存区域")
    public Object editDo(@Param("..") Area area, HttpServletRequest req) {
        try {
            if(Lang.isNotEmpty(area)){
                area.setUpdateBy(ShiroUtils.getSysUserId());
                area.setUpdateTime(new Date());
                areaService.update(area);
            }

            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    /**
     * 删除区域
     */
    @At("/remove/?")
    @Ok("json")
    @RequiresPermissions("sys:area:remove")
    @Slog(tag ="区域", after= "删除区域:${args[0]}")
    public Object remove(String id, HttpServletRequest req) {
        try {
            areaService.delete(id);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    /**
     * 选择菜单树
     */
    @At("/selectTree/?")
    @Ok("th:/sys/area/tree.html")
    public void selectTree(String id, HttpServletRequest req) {
        Area area = null;
        if (Strings.isNotBlank(id)) {
            area = areaService.fetch(id);
        }
        if (area ==null) {
            area =new Area();
            area.setParentId("0");
            area.setName("无");
        }
        req.setAttribute("area", area);
    }

    /**
     * 获取树数据
     *
     * @param parentId
     * @param name
     * @return
     */
    @At
    @Ok("json")
    public List<Map<String, Object>> treeData(@Param("parentId") String parentId,
                                              @Param("name") String name) {
        List<Map<String, Object>> tree = areaService.selectTree(parentId, name);
        return tree;
    }

    public static void getAreaList(List<Districts> list,String pid){
        list.forEach(districts -> {
            Area area =new Area();
            area.setId(R.UU32().toLowerCase());
            area.setParentId(pid);
            area.setAdcode(districts.getAdcode());
            area.setName(districts.getName());
            area.setLevel(districts.getLevel());
            if(districts.getCitycode()!=null && districts.getCitycode().size()>0){
                area.setCitycode(districts.getCitycode().get(0));
            }

            areaList.add(area);
            if(districts.getDistricts()!=null && districts.getDistricts().size()>0){
                getAreaList(districts.getDistricts(),area.getId());
            }
        });
    }

    @At
    @Ok("json")
    public String  initData(){
        //读取文件
        String fileName = "/Users/apple/Desktop/area.txt";
        //读取文件
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8")); //这里可以控制编码
            sb = new StringBuffer();
            String line = null;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //StringBuffer ==> String
        String data = new String(sb);
        Amap amap = Json.fromJson(Amap.class, data);
        if(amap!=null && amap.getDistricts()!=null && amap.getDistricts().size()>0){
            getAreaList(amap.getDistricts(),"0");
        }
//        if(areaList!=null && areaList.size()>0){
//            areaList.forEach(area -> {
//                areaService.insert(area);
//            });
//        }
        return "successs";

    }

}
