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
import javax.servlet.ServletException;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class ConfigReloader implements ConfigReloaderMBean {

    Function function;
    String name;
    static boolean isLoading = false;
    static final Object lock = new Object();

    public ConfigReloader(Function function) {
        this.function = function;
        this.name = "lmo.gw.function." + function.name + ":type=Config,mbean=ConfigReloader";
    }

    public String unregister() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbname = new ObjectName(name);
            mbs.unregisterMBean(mbname);
        } catch (Exception ex) {
            function.logger.warn("unregistering mbean", ex);
        }
        return "OK";
    }

    public String register() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mbname = new ObjectName(name);
            mbs.registerMBean(this, mbname);
        } catch (Exception ex) {
            function.logger.warn("registering mbean", ex);
        }
        return "OK";
    }

    public String reload() throws Exception {
        Properties p = new Properties();
        isLoading = true;
        synchronized (lock) {
            function.destroy(function.logger);
            System.setProperty("lmo.gw.function", function.name);
            File f = new File(System.getProperty("catalina.base") + "/conf/func.properties");
            if (!f.exists()) {
                throw new ServletException("configration not found: " + f.getAbsolutePath());
            }
            try {
                p.load(new FileInputStream(f));
                PropertyConfigurator.configure(p);
            } catch (IOException ex) {
                throw new ServletException(ex);
            }
            try {
                p.load(new FileInputStream(System.getProperty("catalina.base") + "/conf/" + function.name + ".properties"));
            } catch (IOException ex) {
                function.logger.warn(ex.getMessage());
            }
            function.init(function.logger, p);
            isLoading = false;
            lock.notify();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        p.list(writer);
        writer.flush();
        return baos.toString("UTF-8");

    }
}
