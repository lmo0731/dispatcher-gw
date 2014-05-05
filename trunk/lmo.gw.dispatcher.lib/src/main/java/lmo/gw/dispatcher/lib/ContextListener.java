/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.log4j.Logger;

/**
 * Web application lifecycle listener.
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
@WebListener()
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Dispatcher.NAME = sce.getServletContext().getContextPath()
                .replaceAll("^[/]+", "")
                .replaceAll("[/]+$", "");
        Logger logger = Logger.getLogger(Dispatcher.NAME + ".CONFIG");
        Config.reload(logger);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
