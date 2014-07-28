/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib.impl;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.dispatcher.lib.DispatcherException;
import lmo.gw.dispatcher.lib.ErrorHandler;
import lmo.gw.lib.Function;
import org.apache.log4j.Logger;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class DefaultErrorHandler extends ErrorHandler {

    @Override
    public void handle(HttpServletResponse response, Logger logger, DispatcherException ex) throws IOException {
        String res = ex.getMessage();
        PrintWriter out = response.getWriter();
        try {
            response.setContentType(Function.TEXT_PLAIN);
            out.println(res);
        } finally {
            out.close();
        }
    }
}
