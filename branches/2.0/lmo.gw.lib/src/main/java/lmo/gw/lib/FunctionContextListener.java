/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

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
        logger.info("CONTEXT INIT");
        mbean = new ConfigReloader(this);
        try {
            mbean.register();
        } catch (Exception ex) {
            this.logger.error("reloading config", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            mbean.unregister();
        } catch (Exception ex) {
        }
    }

    public String getName() {
        return name;
    }
}
