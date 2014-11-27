/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.annotation.WebListener;
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
            listener.destroyConfig();
        } catch (Exception ex) {
            logger.warn("destroying", ex);
        }
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
        Object ret = null;
        Properties p = new Properties();
        synchronized (lock) {
            isLoading = true;
            try {
                BasicConfigurator.configure();
                String name1 = "lmo.func";
                if (listener.getClass().isAnnotationPresent(WebListener.class)) {
                    WebListener cname = listener.getClass().getAnnotation(WebListener.class);
                    if (!cname.value().isEmpty()) {
                        name1 = cname.value();
                    }
                }
                try {
                    p.load(new FileInputStream(System.getProperty("catalina.base") + "/conf/" + name1 + ".properties"));
                } catch (IOException ex) {
                    logger.warn(ex.getMessage());
                }
                try {
                    p.load(new FileInputStream(System.getProperty("catalina.base") + "/conf/" + listener.getName() + ".properties"));
                } catch (IOException ex) {
                    logger.warn(ex.getMessage());
                }
                for (String k : p.stringPropertyNames()) {
                    String v = p.getProperty(k);
                    v = v.replace("${lmo.gw.function}", listener.getName());
                    p.setProperty(k, v);
                }
                PropertyConfigurator.configure(p);
                try {
                    listener.initConfig(p);
                } catch (Exception ex) {
                    logger.warn("", ex);
                }
            } finally {
                isLoading = false;
                lock.notify();
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        p.list(writer);
        writer.flush();
        try {
            return baos.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return baos.toString();
        }
    }

    public String reload() throws Exception {
        this.unregister();
        return this.register();
    }
}
