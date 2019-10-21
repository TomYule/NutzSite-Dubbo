package io.nutz.nutzsite.module.monitor.services;

import io.nutz.nutzsite.common.service.BaseService;
import io.nutz.nutzsite.module.monitor.models.Logininfor;

/**
 * @Author: Haimming
 * @Date: 2019-10-21 11:18
 * @Version 1.0
 */
public interface LogininforService extends BaseService<Logininfor> {
    public void cleanLogininfor();
}
