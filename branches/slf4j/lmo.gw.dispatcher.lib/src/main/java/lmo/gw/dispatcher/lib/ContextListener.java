/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import lmo.gw.lib.FunctionContextListener;
import org.apache.log4j.Logger;

/**
 * Web application lifecycle listener.
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
@WebListener()
public class ContextListener extends FunctionContextListener {

    public void init(Properties p) throws Exception {
        Dispatcher.NAME = getName();
        Config.reload(logger);
    }

    public void destroy() throws Exception {
    }
}
