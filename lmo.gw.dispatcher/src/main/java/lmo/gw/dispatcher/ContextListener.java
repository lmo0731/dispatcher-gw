/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
@WebListener()
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Config.NAME = sce.getServletContext().getContextPath()
                .replaceAll("^[/]+", "")
                .replaceAll("[/]+$", "");
        Config.reload();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
