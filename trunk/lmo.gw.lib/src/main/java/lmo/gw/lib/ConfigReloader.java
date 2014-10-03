/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class ConfigReloader implements ConfigReloaderMBean {

    ConfigListener listener;
    String name;
    static boolean isLoading = false;
    static final Object lock = new Object();
    Logger logger;

    public ConfigReloader(ConfigListener listener) {
        this.listener = listener;
        this.name = "lmo.gw.function." + this.listener.getName() + ":type=Config,mbean=ConfigReloader";
        this.logger = Logger.getLogger("FUNC." + name + ".CONFIG");
    }

    public String unregister() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbname = new ObjectName(name);
            mbs.unregisterMBean(mbname);
        } catch (Exception ex) {
        }
        return "OK";
    }

    public String register() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbname = new ObjectName(name);
            mbs.registerMBean(this, mbname);
        } catch (Exception ex) {
        }
        return "OK";
    }

    public String reload() throws Exception {
        Properties p = new Properties();
        isLoading = true;
        synchronized (lock) {
            try {
                try {
                    listener.destroy();
                } catch (Exception ex) {
                    logger.warn("", ex);
                }
                BasicConfigurator.configure();
                try {
                    p.load(new FileInputStream(System.getProperty("catalina.base") + "/conf/lmo.func.properties"));
                } catch (IOException ex) {
                    logger.warn(ex.getMessage());
                }
                try {
                    p.load(new FileInputStream(System.getProperty("catalina.base") + "/conf/" + listener.getName() + ".properties"));
                } catch (IOException ex) {
                    logger.warn(ex.getMessage());
                }
                System.setProperty("lmo.gw.function", listener.getName());
                PropertyConfigurator.configure(p);
                try {
                    listener.init(p);
                } catch (Exception ex) {
                    logger.warn("", ex);
                }
            } catch (Exception ex) {
                logger.warn("", ex);
            } finally {
                lock.notify();
                isLoading = false;
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        p.list(writer);

        writer.flush();

        return baos.toString(
                "UTF-8");

    }
}
