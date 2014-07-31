/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.dispatcher.lib;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.Attribute;
import lmo.gw.lib.ConfigReloader;
import org.apache.log4j.Logger;

/**
 *
 * @ munkhochir<lmo0731@gmail.com>
 */
@WebServlet(name = "Dispatcher", urlPatterns = {"/*"})
@MultipartConfig(location = "/tmp", fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class DispatcherServlet extends HttpServlet {

    public static final String LOG = Dispatcher.NAME + ".DISPATCHER";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        while (ConfigReloader.isLoading) {
            System.out.println(LOG + ": Config is loading. WAITING...");
            synchronized (ConfigReloader.lock) {
                try {
                    ConfigReloader.lock.wait(100);
                } catch (InterruptedException ex) {
                }
            }
            System.out.println(LOG + ": Config is loaded.");
        }
        String REQID = String.format("%x%03x", System.currentTimeMillis(), (int) (Math.random() * 0xfff));
        response.setHeader("X-Request-Id", "" + REQID);
        request.setAttribute(Attribute.REQID, "" + REQID);
        Logger logger = Logger.getLogger(LOG + "." + REQID);
        logger.info(request.getMethod() + ": " + request.getRequestURI());
        try {
            Dispatcher d = new Dispatcher("" + REQID);
            d.processRequest(request, response);
        } catch (DispatcherException ex) {
            logger.error("Dispatch error", ex);
            try {
                Config.errorHandler.handle(response, logger, ex);
            } finally {
                response.getWriter().close();
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
