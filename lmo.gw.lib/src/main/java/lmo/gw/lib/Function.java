/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.gw.lib;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.servlet.DispatcherType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lmo.gw.lib.handler.DeleteHandler;
import lmo.gw.lib.handler.GetHandler;
import lmo.gw.lib.handler.PostHandler;
import lmo.gw.lib.handler.PutHandler;
import org.apache.log4j.Logger;

/**
 *
 * @munkhochir<lmo0731@gmail.com>
 */
public abstract class Function extends HttpServlet implements ConfigListener {

    private Logger logger;
    private String name = "Function";
    private ConfigReloader mbean;

    public String getName() {
        return name;
    }

    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config); //To change body of generated methods, choose Tools | Templates.
        String context = config.getServletContext().getContextPath().substring(1);
        String servletName;
        if (this.getClass().isAnnotationPresent(WebServlet.class)) {
            WebServlet servlet = this.getClass().getAnnotation(WebServlet.class);
            if (servlet.urlPatterns() == null || servlet.urlPatterns().length == 0) {
                servletName = config.getServletName();
            } else {
                servletName = servlet.urlPatterns()[0];
            }
        } else {
            servletName = config.getServletName();
        }
        String name = (context + "/" + servletName)
                .replaceAll("^[/]+", "")
                .replaceAll("[/]+$", "")
                .replaceAll("[.]+$", "")
                .replaceAll("^[.]+", "")
                .replaceAll("[.]+", ".")
                .replaceAll("[/]+", "/");
        this.name = name;
        this.logger = Logger.getLogger("FUNC." + name.toUpperCase());
        mbean = new ConfigReloader(this);
        try {
            mbean.register();
        } catch (Exception ex) {
            this.logger.error("reloading config", ex);
        }
    }

    public Function() {
    }

    @Override
    public void destroy() {
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
        destroy(logger);
        mbean.unregister();
    }

    public void init(Properties p) throws Exception {
        this.init(logger, p);
    }

    protected abstract void init(Logger logger, Properties p) throws ServletException;

    protected abstract void destroy(Logger logger);

    protected GetHandler get() {
        return null;
    }

    protected PostHandler post() {
        return null;
    }

    protected PutHandler put() {
        return null;
    }

    protected DeleteHandler delete() {
        return null;
    }

    protected final void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (ConfigReloader.isLoading) {
            synchronized (ConfigReloader.lock) {
                try {
                    ConfigReloader.lock.wait();
                } catch (InterruptedException ex) {
                }
            }
        }
        String REQID = (String) req.getAttribute(Attribute.REQID);
        String funcname = (String) req.getAttribute(Attribute.FUNCNAME);
        Logger logger = Logger.getLogger(funcname + "." + REQID);
        boolean xml = false;
        Handler handler = null;
        Map<String, String> resHeaders = null;
        if (req.getMethod().equals("GET")) {
            handler = this.get();
        } else if (req.getMethod().equals("POST")) {
            handler = this.post();
        } else if (req.getMethod().equals("PUT")) {
            handler = this.put();
        } else if (req.getMethod().equals("DELETE")) {
            handler = this.delete();
        }
        boolean begin = false;
        try {
            if (req.getDispatcherType() != DispatcherType.FORWARD && req.getDispatcherType() != DispatcherType.INCLUDE) {
                throw new FunctionException(HttpServletResponse.SC_FORBIDDEN, "Not gateway request.");
            }
            resp.setCharacterEncoding(req.getCharacterEncoding());
            logger.info("request method: " + req.getMethod());
            logger.info("request query: " + req.getQueryString());
            if (handler == null) {
                throw new FunctionException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method not allowed.");
            }
            ContentBinder binder = null;
            for (Object k : handler.binders.keySet()) {
                if (k instanceof String) {
                    if (req.getContentType().toLowerCase().startsWith(((String) k).toLowerCase())) {
                        binder = (ContentBinder) handler.binders.get(k);
                        break;
                    }
                }
            }
            if (binder == null) {
                String accept = handler.binders.keySet().toString();
                resp.setHeader("Accept", accept.substring(1, accept.length() - 1));
                throw new FunctionException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            }
            Object reqObj = null;
            try {
                reqObj = binder.deserialize(req.getInputStream(), handler.target);
            } catch (ContentBindException ex) {
                throw new FunctionException(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage(), ex);
            }
            FunctionRequest funcReq = handler.getRequest(logger, funcname, reqObj, req);
            FunctionResponse funcRes = new FunctionResponse(resp);
            handler.handle(funcReq, funcRes);
            resp.setStatus(funcRes.getCode());
            Object resObj = funcRes.getResponseObject();
            try {
                binder.serialize(resObj, resp.getOutputStream());
            } catch (ContentBindException ex) {
                throw new FunctionException(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (FunctionException ex) {
            resp.setContentType("text/plain");
            if (ex.getCause() != null) {
                logger.debug("Function error cause", ex);
            }
            resp.setStatus(ex.getCode());
            resp.getWriter().append(ex.getMessage());
        } catch (Exception ex) {
            resp.setContentType("text/plain");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Internal error.", ex);
            resp.getWriter().append("Internal error.");
        } finally {
            try {
                resp.getWriter().flush();
                resp.getWriter().close();
            } catch (Exception ex) {
            }
            logger.info("DONE: " + resp.getStatus());
        }
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected final void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
