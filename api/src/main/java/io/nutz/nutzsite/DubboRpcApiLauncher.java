package io.nutz.nutzsite;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class DubboRpcApiLauncher {

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }

}
