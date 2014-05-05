/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.Attribute;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
@WebServlet(name = "Dispatcher", urlPatterns = {"/*"})
@MultipartConfig(location = "/tmp", fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class DispatcherServlet extends HttpServlet {

    public static final String LOG = Config.LOG + ".DISPATCHER";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (Config.isLoading) {
            try {
                Config.lock.wait(500);
            } catch (InterruptedException ex) {
            }
        }
        String REQID = String.format("%x%03x", System.currentTimeMillis(), (int) (Math.random() * 0xfff));
        response.setHeader("X-Request-Id", "" + REQID);
        request.setAttribute(Attribute.REQID, "" + REQID);
        Logger logger = Logger.getLogger(LOG + "." + REQID);
        logger.info(request.getMethod() + ": " + request.getRequestURI());
        try {
            Dispatcher d = new Dispatcher("" + REQID);
            d.processRequest(request, response);
        } catch (Exception ex) {
            String res = null;
            if (ex instanceof DispatcherException) {
                res = ex.getMessage();
            } else {
                logger.error("Internal error", ex);
                res = "internal error";
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            PrintWriter out = response.getWriter();
            try {
                out.println(Config.serializer.serialize(res));
            } finally {
                out.close();
            }
            logger.info("DONE: " + response.getStatus());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res); //To change body of generated methods, choose Tools | Templates.
    }
}