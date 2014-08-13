/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
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
    public static ConfigReloader mbean;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String name = sce.getServletContext().getContextPath()
                .replaceAll("^[/]+", "")
                .replaceAll("[/]+$", "");
        this.name = name;
        BasicConfigurator.configure();
        logger = Logger.getLogger(name + ".CONTEXT");
        logger.info("CONTEXT INIT");
        String config = "lmo.gw.function";
        if (this.getClass().isAnnotationPresent(WebListener.class)) {
            WebListener webListener = this.getClass().getAnnotation(WebListener.class);
            if (!webListener.value().equals("")) {
                config = webListener.value();
            }
        }
        mbean = new ConfigReloader(this, config);
        try {
            mbean.register();
        } catch (Exception ex) {
            this.logger.error("reloading config", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        mbean.unregister();
        logger.info("CONTEXT DESTROYER");
    }

    public String getName() {
        return name;
    }
}
