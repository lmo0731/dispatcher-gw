/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public class ConfigReloader implements ConfigReloaderMBean {

    ConfigListener listener;
    String name;
    String configFile;
    public static boolean isLoading = false;
    public static final Object lock = new Object();
    Logger logger;

    public ConfigReloader(ConfigListener listener) {
        this(listener, "mn.moogol.func");
    }

    public ConfigReloader(ConfigListener listener, String configFile) {
        this.listener = listener;
        this.configFile = configFile;
        this.name = configFile + "." + this.listener.getName() + ":type=Config,mbean=ConfigReloader";
        this.logger = Logger.getLogger(name + ".CONFIG");
    }

    public void unregister() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbname = new ObjectName(name);
            mbs.unregisterMBean(mbname);
        } catch (Exception ex) {
        }
    }

    public Object register() throws Exception {
        Object ret = null;
        Properties p = new Properties();
        synchronized (lock) {
            isLoading = true;
            try {
                System.setProperty(configFile, listener.getName());
                BasicConfigurator.configure();
                try {
                    p.load(new FileInputStream(System.getProperty("catalina.base") + "/conf/" + configFile + ".properties"));
                } catch (IOException ex) {
                    logger.warn(ex.getMessage());
                }
                try {
                    p.load(new FileInputStream(System.getProperty("catalina.base") + "/conf/" + listener.getName() + ".properties"));
                } catch (IOException ex) {
                    logger.warn(ex.getMessage());
                }
                PropertyConfigurator.configure(p);
                ret = listener.init(p);
            } finally {
                isLoading = false;
                lock.notify();
            }
        }
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbname = new ObjectName(name);
            mbs.registerMBean(this, mbname);
        } catch (Exception ex) {
        }
        return ret;
    }

    public Object reload() throws Exception {
        this.unregister();
        try {
            listener.destroy();
        } catch (Exception ex) {
            logger.warn("destroying", ex);
        }
        return this.register();
    }
}
