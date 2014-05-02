/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static lmo.gw.dispatcher.Config.functions;
import static lmo.gw.dispatcher.Config.userperms;
import static lmo.gw.dispatcher.Config.users;
import static lmo.gw.dispatcher.Config.userips;
import lmo.gw.lib.Attribute;
import lmo.gw.lib.Node;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
public class Dispatcher {

    Logger logger;
    String funcname;

    public Dispatcher(String REQID) {
        logger = Logger.getLogger(Config.LOG + ".DISPATCHER." + REQID);
    }

    protected String authenticate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DispatcherException {
        String authorization = request.getHeader("Authorization");
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + Config.NAME + "\"");
        if (authorization == null || !authorization.startsWith("Basic ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new DispatcherException("authentication required");
        }
        String basicAuth = authorization.substring(authorization.indexOf(" ") + 1);
        String user, pass;
        try {
            byte[] decodedBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(basicAuth);
            String userpass = new String(decodedBytes);
            String[] k = userpass.split(":", 2);
            user = k[0];
            pass = k[1];
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new DispatcherException("authentication required");
        }
        logger.debug("user: " + user);
        String method = request.getMethod();
        String functionPath = request.getRequestURI().replaceFirst("/[^/]+/?", "");
        Node n = Config.functionPaths.get(functionPath);
        if (n != null && n.getValue() != null) {
            funcname = n.getValue();
            request.setAttribute(Attribute.PATHPARAMS, n.getMatches());
        }
        if (funcname == null || !functions.containsKey(funcname)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new DispatcherException("function not found");
        }
        if (!pass.equals(users.get(user))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new DispatcherException("authentication fail");
        }
        if (!userips.containsKey(user) || (!userips.get(user).contains(request.getRemoteAddr()) && !userips.get(user).contains("*"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new DispatcherException("permission denied. " + request.getRemoteAddr());
        }
        String perm = (funcname + "#" + method);
        String wildcard = (funcname + "#*");
        logger.debug("required perm: " + perm);
        Set<String> roles = userperms.get(user);
        if (roles == null || (!roles.contains(perm) && !roles.contains(wildcard))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new DispatcherException("permission denied");
        }
        return Config.functions.get(funcname);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, DispatcherException {
        String REQID = (String) request.getAttribute(Attribute.REQID);
        String functionPath = this.authenticate(request, response);
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
            throw new DispatcherException("internal error");
        }
        RequestDispatcher dispatcher = cxt.getRequestDispatcher("/" + servletPath);
        if (dispatcher == null) {
            logger.error("path not found: /" + servletPath);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new DispatcherException("internal error");
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
