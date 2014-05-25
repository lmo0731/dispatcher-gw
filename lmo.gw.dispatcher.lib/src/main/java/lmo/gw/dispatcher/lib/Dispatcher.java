/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.Attribute;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class Dispatcher {

    Logger logger;
    String funcname;
    public static String NAME = "GW";
    Authenticator authenticator;

    public Dispatcher(String REQID, Authenticator authenticator) {
        logger = Logger.getLogger(Dispatcher.NAME + ".DISPATCHER." + REQID);
        this.authenticator = authenticator;
    }

    protected String getFunction(HttpServletRequest request, HttpServletResponse response) throws DispatcherException {
        String functionPath = request.getRequestURI().replaceFirst("/?" + Dispatcher.NAME + "/?", "/");
        logger.debug("path: " + functionPath);
        ArrayList<String> matches = new ArrayList<String>();
        String funcname = null;
        Node n = Config.functionPaths.get(functionPath, matches);
        if (n != null && n.getValue() != null) {
            funcname = n.getValue();
            request.setAttribute(Attribute.PATHPARAMS, matches);
            request.setAttribute(Attribute.FUNCNAME, funcname);
        }
        if (funcname == null || !Config.functions.containsKey(funcname)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new DispatcherException("requested function not found");
        }
        return funcname;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, DispatcherException {
        String REQID = (String) request.getAttribute(Attribute.REQID);
        String funcname = getFunction(request, response);
        if (authenticator != null) {
            authenticator.authenticate(request, response, funcname, logger);
        }
        String functionPath = Config.functions.get(funcname);
        String path[] = functionPath.split("!", 2);
        String contextName = path[0].replaceFirst("^/", "");
        String servletPath = "";
        if (path.length > 1) {
            servletPath = path[1].replaceFirst("^/", "");
        }
        logger.debug("function context: /" + contextName + ", servlet: /" + servletPath);
        ServletContext cxt = request.getServletContext().getContext("/" + contextName);
        if (cxt == null) {
            logger.error("context not found: /" + contextName);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new DispatcherException("function not found");
        }
        RequestDispatcher dispatcher = cxt.getRequestDispatcher("/" + servletPath);
        if (dispatcher == null) {
            logger.error("path not found: /" + servletPath);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new DispatcherException("function not found");
        }
        logger.info("Dispatching request to /" + contextName + "/" + servletPath);
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String s = request.getReader().readLine();
                if (s == null) {
                    break;
                }
                sb.append(s);
            }
            logger.info("request: " + sb.toString());
            request.setAttribute(Attribute.REQUEST, sb.toString());
            dispatcher.forward(request, response);
        } catch (FileNotFoundException ex) {
            logger.error("function not found", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new DispatcherException("function not found");
        } catch (Exception ex) {
            logger.error("function error", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new DispatcherException("internal error");
        }
    }
}
