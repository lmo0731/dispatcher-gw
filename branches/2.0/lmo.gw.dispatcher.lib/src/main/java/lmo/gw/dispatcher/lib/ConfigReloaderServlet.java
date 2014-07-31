/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.Function;
import lmo.gw.lib.FunctionException;
import lmo.gw.lib.FunctionRequest;
import lmo.gw.lib.FunctionResponse;
import lmo.gw.lib.handler.GetHandler;

/**
 *
 * @author lmoo
 */
@WebServlet(name = "ConfigReloaderServlet", urlPatterns = {"/ConfigReloaderServlet"})
public class ConfigReloaderServlet extends Function {

    @Override
    protected GetHandler get() {
        return new GetHandler() {

            @Override
            public void handle(FunctionRequest<Object> request, FunctionResponse response) throws FunctionException {
                try {
                    response.setResponseObject(DispatcherContextListener.mbean.reload());
                    response.setCode(HttpServletResponse.SC_OK);
                } catch (Exception ex) {
                    response.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
        };
    }

}
