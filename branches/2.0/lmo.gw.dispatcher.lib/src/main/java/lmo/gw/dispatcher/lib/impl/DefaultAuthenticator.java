/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib.impl;

import lmo.gw.dispatcher.lib.Authenticator;
import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.dispatcher.lib.Dispatcher;
import lmo.gw.dispatcher.lib.DispatcherException;
import org.apache.log4j.Logger;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public class DefaultAuthenticator extends Authenticator {

    public void authenticate(HttpServletRequest request, HttpServletResponse response, String funcname, Logger logger) throws ServletException, IOException, DispatcherException {
        String authorization = request.getHeader("Authorization");
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + Dispatcher.NAME + "\"");
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
        if (!pass.equals(DefaultConfigurator.users.get(user))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new DispatcherException("authentication fail");
        }
        logger.debug("ip: '" + request.getRemoteAddr() + "'");
        logger.debug("ips: " + DefaultConfigurator.userips.get(user));
        if (!DefaultConfigurator.userips.containsKey(user)
                || (!DefaultConfigurator.userips.get(user).contains(request.getRemoteAddr().trim())
                && !DefaultConfigurator.userips.get(user).contains("*"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new DispatcherException("permission denied. " + request.getRemoteAddr());
        }
        String perm = (funcname + "#" + request.getMethod());
        String wildcard = (funcname + "#*");
        logger.debug("required perm: " + perm);
        Set<String> roles = DefaultConfigurator.userperms.get(user);
        if (roles == null || (!roles.contains(perm) && !roles.contains(wildcard))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new DispatcherException("permission denied");
        }
    }
}
