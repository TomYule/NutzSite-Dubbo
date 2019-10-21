package io.nutz.nutzsite.common.manager.factory;


import com.alibaba.dubbo.config.annotation.Service;
import eu.bitwalker.useragentutils.UserAgent;
import io.nutz.nutzsite.common.utils.AddressUtils;
import io.nutz.nutzsite.common.utils.LogUtils;
import io.nutz.nutzsite.module.monitor.models.Logininfor;
import io.nutz.nutzsite.module.monitor.services.LogininforService;
import io.nutz.nutzsite.module.monitor.services.OperLogService;
import io.nutz.nutzsite.module.monitor.services.UserOnlineService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author liuhulu
 */
@IocBean
public class AsyncFactory {
    private static Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    @Inject
    private UserOnlineService userOnlineService;
    @Inject
    private OperLogService operLogService;
    @Inject
    private LogininforService logininforService;

    /**
     * 记录登陆信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息
     * @param args     列表
     * @return 任务task
     */
    public TimerTask recordLogininfor(String username, boolean status, HttpServletRequest req, String message, Object... args) {
        UserAgent userAgent = UserAgent.parseUserAgentString(Mvcs.getReq().getHeader("User-Agent"));
        String ip = Lang.getIP(req);
        sys_user_logger.info("登录IP:" + ip);
        return new TimerTask() {
            @Override
            public void run() {
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(AddressUtils.getRealAddressByIP(ip));
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                Logininfor logininfor = new Logininfor();
                logininfor.setLoginName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                logininfor.setStatus(status);
                logininfor.setLoginTime(new Date());
                // 日志状态
//                if (Constants.LOGIN_SUCCESS.equals(status) || Constants.LOGOUT.equals(status))
//                {
//                    logininfor.setStatus(Constants.SUCCESS);
//                }
//                else if (Constants.LOGIN_FAIL.equals(status))
//                {
//                    logininfor.setStatus(Constants.FAIL);
//                }
                // 插入数据
                logininforService.insert(logininfor);
            }
        };
    }


}
