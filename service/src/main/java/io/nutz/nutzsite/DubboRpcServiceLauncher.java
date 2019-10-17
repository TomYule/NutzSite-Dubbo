package io.nutz.nutzsite;

import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.nio.charset.Charset;

@IocBean(create = "init", depose = "depose")
public class DubboRpcServiceLauncher {
    private static final Log log = Logs.get();

    @Inject
    private Dao dao;

    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("io.nutz.nutzsite");
        nb.run();
    }

    /**
     * NB自身初始化完成后会调用这个方法
     */
    public void init() {
        // 环境检查
        if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
            log.warn("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
        }

        // 创建数据库
        Daos.createTablesInPackage(dao, "io.nutz.nutzsite", false);
    }


    public void depose() {}

}
