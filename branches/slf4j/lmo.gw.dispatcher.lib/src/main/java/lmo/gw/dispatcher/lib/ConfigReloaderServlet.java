/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.Function;
import lmo.gw.lib.FunctionException;
import lmo.gw.lib.FunctionRequest;
import lmo.gw.lib.FunctionResponse;
import lmo.gw.lib.handler.GetHandler;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
@WebServlet(name = "ConfigReloaderServlet", urlPatterns = "/ConfigReloader")
public class ConfigReloaderServlet extends Function {

    @Override
    protected void init(Logger logger, Properties p) throws ServletException {
    }

    @Override
    protected void destroy(Logger logger) {
    }

    @Override
    protected GetHandler get() {
        return new GetHandler() {
            @Override
            public void handle(FunctionRequest<Object> request, FunctionResponse response) throws FunctionException {
                response.setResponseObject(Config.reload(request.getLogger()));
                response.setCode(HttpServletResponse.SC_OK);
            }
        };
    }
}
