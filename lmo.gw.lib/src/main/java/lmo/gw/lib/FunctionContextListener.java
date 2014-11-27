/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 *
 * WebListener annotation-g implement hiine
 */
public abstract class FunctionContextListener implements ServletContextListener, ConfigListener {

    protected Logger logger;
    protected String name;
    ConfigReloader mbean;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String name = sce.getServletContext().getContextPath()
                .replaceAll("^[/]+", "")
                .replaceAll("[/]+$", "");
        this.name = name;
        BasicConfigurator.configure();
        logger = Logger.getLogger("FUNC." + name + ".CONTEXT");
        mbean = new ConfigReloader(this);
        mbean.register();
        logger.info("CONTEXT INIT");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        mbean.unregister();
        logger.info("CONTEXT DESTROYED");
    }

    public String getName() {
        return name;
    }

    public void initConfig(Properties p) throws Exception {
        init(p);
    }

    public void destroyConfig() throws Exception {
        destroy();
    }

    public abstract void init(Properties p) throws Exception;

    public abstract void destroy() throws Exception;
}
