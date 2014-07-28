/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author munkhochir <munkhochir@mobicom.mn>
 */
public abstract class Authenticator {

    public abstract void authenticate(HttpServletRequest request, HttpServletResponse response, String funcname, Logger logger) throws ServletException, IOException, DispatcherException;
}
